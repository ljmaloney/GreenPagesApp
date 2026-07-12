package com.green.yp.app.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }


            install(Logging) {
                level = LogLevel.INFO
            }

            // Centralized HTTP response validation: map 4xx -> ClientRequestException, 5xx -> ServerResponseException
            HttpResponseValidator {
                validateResponse { response ->
                    val code = response.status.value
                    if (code >= 400) {
                        val errorText = response.bodyAsText()
                        when (code) {
                            in 400..499 -> throw ClientRequestException(response, errorText)
                            in 500..599 -> throw ServerResponseException(response, errorText)
                        }
                    }
                }
            }

            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
            }
        }
    }
}