package com.green.yp.app.shared.repository

import co.touchlab.kermit.Logger
import com.green.yp.app.shared.api.EmailContactApi
import com.green.yp.app.shared.dto.email.EmailValidationRequest
import com.green.yp.app.shared.dto.message.MessageRequest
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmailContactRepositoryImpl(private val emailContactApi: EmailContactApi) : EmailContactRepository {

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val log = Logger.withTag("green.yp.app.shared.repository.EmailContactRepository")

    override suspend fun validateEmail(externRef: String, emailAddress: String, token: String): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null
        
        val normalizedEmail = emailAddress.trim().lowercase()
        log.d("Validating email address - $normalizedEmail")

        return runCatching {
            emailContactApi.validateEmail(
                EmailValidationRequest(
                    externRef = externRef,
                    emailAddress = normalizedEmail,
                    token = token
                )
            )
            
            _errorMessage.value = null
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Unable to validate email address using the token provided"
                is ServerResponseException -> "Unexpected system error. Please try again later"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            _errorMessage.value = message
            log.e("Exception validating email", throwable)
        }.also {
            _isLoading.value = false
        }
    }

    override fun clearError() {
        _errorMessage.value = null
    }

    override suspend fun sendContactMessage(message: MessageRequest): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null

        val normalizedRequest = message.copy(
            emailAddress = message.emailAddress.trim().lowercase(),
            name = message.name.trim(),
            phoneNumber = message.phoneNumber.trim(),
            subject = message.subject.trim(),
            message = message.message.trim(),
            companyName = message.companyName?.trim()
        )

        log.d("Sending contact message request for type - ${normalizedRequest.requestType.name}")

        return runCatching {
            emailContactApi.sendContactMessage(normalizedRequest)
            _errorMessage.value = null
        }.onFailure { throwable ->
            val error = when (throwable) {
                is ClientRequestException -> throwable.message
                    .takeUnless { it.isBlank() }
                    ?: "Unable to send contact message. Please check your details and try again"
                is ServerResponseException -> throwable.message
                    .takeUnless { it.isBlank() }
                    ?: "Unexpected system error. Please try again later"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            _errorMessage.value = error
            log.e("Exception sending contact message", throwable)
        }.also {
            _isLoading.value = false
        }
    }
}
