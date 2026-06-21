package com.green.yp.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import android.util.Log
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidLocationManager(private val context: Context) : LocationManager {
    private val TAG = "LocationManager"
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _locationUpdates = MutableStateFlow<UserLocation?>(null)
    override val locationUpdates: StateFlow<UserLocation?> = _locationUpdates.asStateFlow()

    private var locationCallback: LocationCallback? = null
    private var isUpdating = false

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
        .setMinUpdateIntervalMillis(1000L)
        .setMaxUpdateDelayMillis(2000L)
        .build()

    init {
        Log.d(TAG, "LocationManager initialized")
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun getCurrentLocation(): UserLocation? {
        return suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "getCurrentLocation called")
            if (!hasLocationPermission()) {
                Log.w(TAG, "No location permission")
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val cancellationTokenSource = CancellationTokenSource()
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }

            try {
                Log.d(TAG, "Requesting lastLocation")
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(TAG, "Got lastLocation: lat=${location.latitude}, lon=${location.longitude}")
                        val result = UserLocation(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            altitude = location.altitude
                        )
                        _locationUpdates.value = result
                        continuation.resume(result)
                    } else {
                        Log.d(TAG, "lastLocation is null, trying fresh getCurrentLocation")
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cancellationTokenSource.token
                        ).addOnSuccessListener { fresh ->
                            Log.d(TAG, "Got fresh location: $fresh")
                            val result = fresh?.let {
                                UserLocation(
                                    latitude = it.latitude,
                                    longitude = it.longitude,
                                    accuracy = it.accuracy,
                                    altitude = it.altitude
                                )
                            }
                            if (result != null) {
                                _locationUpdates.value = result
                            }
                            continuation.resume(result)
                        }.addOnFailureListener { ex ->
                            Log.e(TAG, "fresh getCurrentLocation failed: $ex")
                            continuation.resume(null)
                        }
                    }
                }.addOnFailureListener { ex ->
                    Log.e(TAG, "lastLocation failed: $ex")
                    continuation.resume(null)
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException in getCurrentLocation: $e")
                continuation.resume(null)
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates called, hasPermission=${hasLocationPermission()}")
        if (!hasLocationPermission()) {
            Log.e(TAG, "No location permission, cannot start updates")
            return
        }

        if (isUpdating) {
            Log.d(TAG, "Already updating location")
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val userLocation = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        altitude = location.altitude
                    )
                    _locationUpdates.value = userLocation
                    Log.d(TAG, "onLocationResult: lat=${location.latitude}, lon=${location.longitude}, acc=${location.accuracy}")
                }
            }
        }

        try {
            Log.d(TAG, "Calling requestLocationUpdates")
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            isUpdating = true
            Log.d(TAG, "requestLocationUpdates succeeded")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException in startLocationUpdates: $e")
            locationCallback = null
            isUpdating = false
        }
    }

    override fun stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates called")
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
            isUpdating = false
            Log.d(TAG, "Location updates stopped")
        }
    }

    override suspend fun isLocationAvailable(): Boolean {
        val available = hasLocationPermission()
        Log.d(TAG, "isLocationAvailable: $available")
        return available
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val result = fineLocation || coarseLocation
        Log.d(TAG, "hasLocationPermission: fine=$fineLocation, coarse=$coarseLocation, result=$result")
        return result
    }
}

private object AndroidLocationManagerHolder {
    private var instance: AndroidLocationManager? = null

    fun get(context: Context): AndroidLocationManager {
        return instance ?: AndroidLocationManager(context.applicationContext).also {
            instance = it
        }
    }
}

actual fun getLocationManager(): LocationManager {
    return try {
        val context = ApplicationContext.get()
        AndroidLocationManagerHolder.get(context)
    } catch (_: IllegalStateException) {
        // Fallback for cases where ApplicationContext is not initialized (e.g., Compose Preview)
        PreviewLocationManager()
    }
}

private class PreviewLocationManager : LocationManager {
    override suspend fun getCurrentLocation(): UserLocation? = null
    override fun startLocationUpdates() {}
    override fun stopLocationUpdates() {}
    override suspend fun isLocationAvailable(): Boolean = false
    override val locationUpdates: StateFlow<UserLocation?> = MutableStateFlow<UserLocation?>(null).asStateFlow()
}