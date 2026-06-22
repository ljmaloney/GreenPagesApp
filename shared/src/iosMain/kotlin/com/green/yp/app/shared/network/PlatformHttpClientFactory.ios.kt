package com.green.yp.app.shared.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual class PlatformHttpClientFactory actual constructor() {

    actual fun createEngine(): HttpClientEngine {
        return Darwin.create()
    }
}