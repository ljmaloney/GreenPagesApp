package com.green.yp.app.config

expect object PlatformConfig {
    val baseUrl: String
    val squareApplicationId: String
    val squareLocationId: String
    val isDebug: Boolean
}
