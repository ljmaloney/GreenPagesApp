package com.green.yp.app

import kotlinx.coroutines.flow.StateFlow

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null
)

interface LocationManager {
    val locationUpdates: StateFlow<UserLocation?>
    suspend fun getCurrentLocation(): UserLocation?
    fun startLocationUpdates()
    fun stopLocationUpdates()
    suspend fun isLocationAvailable(): Boolean
}

expect fun getLocationManager(): LocationManager