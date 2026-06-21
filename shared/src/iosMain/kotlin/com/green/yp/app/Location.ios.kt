package com.green.yp.app

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class IOSLocationManager : LocationManager {
    private val locationManager = CLLocationManager()

    private val _locationUpdates = MutableStateFlow<UserLocation?>(null)
    override val locationUpdates: StateFlow<UserLocation?> = _locationUpdates.asStateFlow()

    private val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            println("locationManager didUpdateLocations called")
            @Suppress("UNCHECKED_CAST")
            val locations = didUpdateLocations as List<platform.CoreLocation.CLLocation>

            locations.lastOrNull()?.let { location ->
                val lat = location.coordinate.useContents { latitude }
                val lon = location.coordinate.useContents { longitude }
                println("Location update: $lat, $lon")
                val userLocation = UserLocation(
                    latitude = lat,
                    longitude = lon,
                    accuracy = location.horizontalAccuracy.takeIf { it >= 0f }?.toFloat(),
                    altitude = location.altitude.takeIf { it != 0.0 }
                )
                _locationUpdates.value = userLocation
            }
        }

        override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: platform.CoreLocation.CLAuthorizationStatus) {
            println("locationManager didChangeAuthorizationStatus: $didChangeAuthorizationStatus")
            when (didChangeAuthorizationStatus) {
                platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse,
                platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways -> {
                    println("Authorization granted, starting updates")
                    locationManager.startUpdatingLocation()
                }
                platform.CoreLocation.kCLAuthorizationStatusDenied,
                platform.CoreLocation.kCLAuthorizationStatusRestricted -> {
                    println("Authorization denied/restricted")
                }
                else -> {
                    println("Authorization status unknown: $didChangeAuthorizationStatus")
                }
            }
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            println("Location manager error: ${didFailWithError.localizedDescription}")
        }
    }

    init {
        println("Initializing IOSLocationManager")
        locationManager.setDelegate(delegate)
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    override suspend fun getCurrentLocation(): UserLocation? {
        startLocationUpdates()
        return try {
            withTimeoutOrNull(5000L) {
                locationUpdates.first { it != null }
            }
        } finally {
            // We want to keep location as long as app is open, so maybe don't stop here 
            // but the interface has stopLocationUpdates.
            // For persistence, we keep the last value in _locationUpdates
        }
    }

    override fun startLocationUpdates() {
        locationManager.startUpdatingLocation()
    }

    override fun stopLocationUpdates() {
        locationManager.stopUpdatingLocation()
    }

    override suspend fun isLocationAvailable(): Boolean {
        return true // iOS handles permission separately
    }

    companion object {
        val instance: IOSLocationManager by lazy { IOSLocationManager() }
    }
}

actual fun getLocationManager(): LocationManager {
    return IOSLocationManager.instance
}
