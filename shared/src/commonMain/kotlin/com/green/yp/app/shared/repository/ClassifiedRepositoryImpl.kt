package com.green.yp.app.shared.repository

import co.touchlab.kermit.Logger
import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.dto.ResponseWrapper
import com.green.yp.app.shared.dto.classified.ClassifiedImageUpload
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ImageGallery
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class ClassifiedRepositoryImpl(private val classifiedApi: ClassifiedApi) : ClassifiedRepository {

    private val json = Json { ignoreUnknownKeys = true }

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

    private val log = Logger.withTag("green.yp.app.shared.di.repository.ClassifiedRepository")

    override suspend fun createClassifiedAd(request: ClassifiedRequest): Result<ClassifiedResponse> {
        _isLoading.value = true
        _errorMessage.value = null
        log.d("Creating classified ad - $request")
        return runCatching {
            val result = classifiedApi.createClassifiedAd(request)
            
            result.errorMessageApi?.let { error ->
                _errorMessage.value = error.displayMessage
                log.e("Error creating classified ad - $error")
                throw IllegalStateException(error.displayMessage)
            }
            log.d("Classified ad created - ${result.response}")
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

//    override suspend fun validateClassifiedEmail(
//        classifiedId: Uuid,
//        emailAddress: String,
//        token: String
//    ): Result<Unit> {
//        _isLoading.value = true
//        _errorMessage.value = null
//        _isValidated.value = false
//
//        return runCatching {
//            // Trim and lowercase email, and trim token to ensure exact match with server records
//            val result = classifiedApi.validateClassifiedEmail(
//                classifiedId,
//                emailAddress.trim().lowercase(),
//                token.trim()
//            )
//
//            result.errorMessageApi?.let { error ->
//                _errorMessage.value = error.displayMessage
//                throw IllegalStateException(error.displayMessage)
//            }
//
//            _isValidated.value = true
//            _errorMessage.value = null
//            Unit
//        }.onFailure { throwable ->
//            val message = when (throwable) {
//                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
//                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
//                is ResponseException -> "Network error: ${throwable.response.status.value}"
//                else -> throwable.message ?: "Unknown error"
//            }
//            _errorMessage.value = message
//        }.also {
//            _isLoading.value = false
//        }
//    }

    override suspend fun uploadImage(
        request: ClassifiedImageUpload
    ): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null
        log.d("Uploading image: ${request.fileName}")

        return runCatching {
            val filePart = PartData.FileItem(
                provider = { ByteReadChannel(request.bytes) },
                dispose = {},
                partHeaders = Headers.build {
                    append(HttpHeaders.ContentType, request.contentType)
                    append(
                        HttpHeaders.ContentDisposition,
                        "form-data; name=\"file\"; filename=\"${request.fileName}\""
                    )
                }
            )

            classifiedApi.uploadImage(
                classifiedId = request.classifiedId,
                body = MultiPartFormDataContent(listOf(filePart)),
                imageFilename = request.fileName,
                imageDescription = request.description
            )

            _errorMessage.value = null
            Unit
        }.onFailure { throwable ->
            val message = parseUploadError(throwable)
            log.e("Upload image failed: $message")
            _errorMessage.value = message
        }.also {
            _isLoading.value = false
        }
    }

    private suspend fun parseUploadError(throwable: Throwable): String {
        return when (throwable) {
            is ResponseException -> {
                val response = throwable.response
                try {
                    val body = response.bodyAsText()
                    val wrapper = json.decodeFromString<ResponseWrapper<Unit?>>(body)
                    wrapper.errorMessageApi?.displayMessage ?: "Error: ${response.status.value}"
                } catch (_: Exception) {
                    "Error: ${response.status.value}"
                }
            }
            else -> throwable.message ?: "Unknown error"
        }
    }

    override suspend fun processClassifiedPayment(payment: ClassifiedPayment): Result<ClassifiedPaymentResponse> {
        _isLoading.value = true
        _errorMessage.value = null
        log.d("processClassifiedPayment - $payment")
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
        log.d("getClassifiedImageGallery - classifiedId=$classifiedId")
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
            val is404 = throwable is ClientRequestException && throwable.response.status.value == 404
            log.d("getClassifiedImageGallery - classifiedId=$classifiedId - error=${throwable.message}")
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            log.d("getClassifiedImageGallery - classifiedId=$classifiedId - error=${throwable.message}")
            if (!is404) {
                _errorMessage.value = message
            } else {
                // If 404, we just clear the gallery and don't set an error message
                _imageGallery.value = emptyList()
            }
        }.also {
            _isLoading.value = false
        }
    }

    override suspend fun getClassified(classifiedId: Uuid): Result<ClassifiedResponse> {
        _isLoading.value = true
        _errorMessage.value = null
        log.d("getClassified - classifiedId=$classifiedId")
        return runCatching {
            val result = classifiedApi.getClassified(classifiedId)

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
}
