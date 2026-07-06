package com.green.yp.app.shared.repository

import co.touchlab.kermit.Logger
import com.green.yp.app.shared.api.EmailContactApi
import com.green.yp.app.shared.dto.email.EmailValidationRequest
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
            val result = emailContactApi.validateEmail(
                EmailValidationRequest(
                    externRef = externRef,
                    emailAddress = normalizedEmail,
                    token = token
                )
            )
            
            result.errorMessageApi?.let { error ->
                _errorMessage.value = error.displayMessage
                log.e("Error validating email - $error")
                throw IllegalStateException(error.displayMessage)
            }
            
            _errorMessage.value = null
            Unit
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
}
