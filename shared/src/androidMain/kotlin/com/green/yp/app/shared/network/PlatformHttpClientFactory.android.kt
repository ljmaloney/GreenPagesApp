package com.green.yp.app.shared.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual class PlatformHttpClientFactory actual constructor() {

    actual fun createEngine(): HttpClientEngine {
        return OkHttp.create()
    }
}