package com.green.yp.app.shared.repository

import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ImageGallery
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import com.green.yp.app.shared.dto.classified.ClassifiedImageUpload
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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

    private val _imageGallery = MutableStateFlow<List<ImageGallery>>(emptyList())
    override val imageGallery: StateFlow<List<ImageGallery>> = _imageGallery.asStateFlow()

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

    override suspend fun uploadImage(
        request: ClassifiedImageUpload
    ): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null

        return runCatching {
            val formData = formData {
                append(
                    key = "file",
                    value = request.bytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, request.contentType)
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=${request.fileName}"
                        )
                    }
                )
            }

            val body = MultiPartFormDataContent(formData)

            val result = classifiedApi.uploadImage(
                classifiedId = request.classifiedId,
                file = body,
                imageFilename = request.fileName,
                imageDescription = request.description
            )

            result.errorMessageApi?.let { error ->
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }
            
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

    override suspend fun getClassifiedImageGallery(classifiedId: Uuid): Result<List<ImageGallery>> {
        _isLoading.value = true
        _errorMessage.value = null

        return runCatching {
            val result = classifiedApi.getClassifiedImageGallery(classifiedId)

            result.errorMessageApi?.let { error ->
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }

            val response = result.response
            _imageGallery.value = response
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
