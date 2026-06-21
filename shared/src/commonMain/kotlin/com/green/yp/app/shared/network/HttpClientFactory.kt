package com.green.yp.app.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect class PlatformHttpClientFactory() {
    fun createEngine(): HttpClientEngine
}

object HttpClientFactory {

    fun create(
        baseUrl: String
    ): HttpClient {

        return HttpClient(
            PlatformHttpClientFactory().createEngine()
        ) {

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = false
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
            }


            install(Logging) {
                level = LogLevel.ALL
            }

            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
            }
        }
    }
}