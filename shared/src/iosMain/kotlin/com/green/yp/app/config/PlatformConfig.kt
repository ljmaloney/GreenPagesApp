package com.green.yp.app.config

actual object PlatformConfig {
    actual val baseUrl = BuildKonfig.GREENYP_SERVICE_URL
    actual val squareApplicationId = BuildKonfig.SQUARE_APPLICATION_ID
    actual val squareLocationId = BuildKonfig.SQUARE_LOCATION_ID
    actual val isDebug = true
}
