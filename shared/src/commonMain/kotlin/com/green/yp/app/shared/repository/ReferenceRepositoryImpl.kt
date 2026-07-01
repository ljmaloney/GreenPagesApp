package com.green.yp.app.shared.repository

import com.green.yp.app.shared.api.ReferenceApi
import com.green.yp.app.shared.dto.reference.LineOfBusiness
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.ktor.client.plugins.*

class ReferenceRepositoryImpl(
    private val referenceApi: ReferenceApi
) : ReferenceRepository {

    private val _linesOfBusiness = MutableStateFlow<List<LineOfBusiness>>(emptyList())
    override val linesOfBusiness: StateFlow<List<LineOfBusiness>> = _linesOfBusiness.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    override suspend fun getLinesOfBusiness(): Result<List<LineOfBusiness>> {
        println("ReferenceRepositoryImpl: getLinesOfBusiness called")
        return runCatching {
            val result = referenceApi.getLinesOfBusiness()
            println("ReferenceRepositoryImpl: API response received")

            result.errorMessageApi?.let { error ->
                println("ReferenceRepositoryImpl: API error: ${error.displayMessage}")
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }
            val data = result.response
            _linesOfBusiness.value = data
            _errorMessage.value = null
            data
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            println("ReferenceRepositoryImpl: getLinesOfBusiness failed with: $message")
            _errorMessage.value = message
        }
    }
}
