package com.green.yp.app.shared.repository

import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.uuid.Uuid

class ClassifiedRepositoryImpl(private val classifiedApi: ClassifiedApi) : ClassifiedRepository {

    private val _createdAd = MutableStateFlow<ClassifiedResponse?>(null)
    override val createdAd: StateFlow<ClassifiedResponse?> = _createdAd.asStateFlow()

    private val _paymentResponse = MutableStateFlow<ClassifiedPaymentResponse?>(null)
    override val paymentResponse: StateFlow<ClassifiedPaymentResponse?> = _paymentResponse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isValidated = MutableStateFlow(false)
    override val isValidated: StateFlow<Boolean> = _isValidated.asStateFlow()

    override suspend fun createClassifiedAd(request: ClassifiedRequest): Result<ClassifiedResponse> {
        _isLoading.value = true
        _errorMessage.value = null
        
        return runCatching {
            val result = classifiedApi.createClassifiedAd(request)
            
            result.errorMessageApi?.let { error ->
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }
            
            val response = result.response
            _createdAd.value = response
            _errorMessage.value = null
            response
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            _errorMessage.value = message
        }.also {
            _isLoading.value = false
        }
    }

    override suspend fun validateClassifiedEmail(
        classifiedId: Uuid,
        emailAddress: String,
        token: String
    ): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null
        _isValidated.value = false

        return runCatching {
            val result = classifiedApi.validateClassifiedEmail(classifiedId, emailAddress, token)

            result.errorMessageApi?.let { error ->
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }

            _isValidated.value = true
            _errorMessage.value = null
            Unit
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            _errorMessage.value = message
        }.also {
            _isLoading.value = false
        }
    }

    override suspend fun processClassifiedPayment(payment: ClassifiedPayment): Result<ClassifiedPaymentResponse> {
        _isLoading.value = true
        _errorMessage.value = null

        return runCatching {
            val result = classifiedApi.processClassifiedPayment(payment)

            result.errorMessageApi?.let { error ->
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }

            val response = result.response
            _paymentResponse.value = response
            _errorMessage.value = null
            response
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            _errorMessage.value = message
        }.also {
            _isLoading.value = false
        }
    }
}
