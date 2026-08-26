package com.green.yp.app.shared.repository

import com.green.yp.app.shared.dto.message.MessageRequest
import kotlinx.coroutines.flow.StateFlow

interface EmailContactRepository {
    val errorMessage: StateFlow<String?>
    val isLoading: StateFlow<Boolean>
    
    suspend fun validateEmail(externRef: String, emailAddress: String, token: String): Result<Unit>
    fun clearError()

    suspend fun sendContactMessage(message: MessageRequest): Result<Unit>
}
