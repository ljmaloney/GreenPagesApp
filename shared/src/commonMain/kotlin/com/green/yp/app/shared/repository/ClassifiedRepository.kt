package com.green.yp.app.shared.repository

import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import kotlinx.coroutines.flow.StateFlow
import kotlin.uuid.Uuid

interface ClassifiedRepository {
    val createdAd: StateFlow<ClassifiedResponse?>
    val paymentResponse: StateFlow<ClassifiedPaymentResponse?>
    val errorMessage: StateFlow<String?>
    val isLoading: StateFlow<Boolean>
    val isValidated: StateFlow<Boolean>

    suspend fun createClassifiedAd(request: ClassifiedRequest): Result<ClassifiedResponse>
    suspend fun validateClassifiedEmail(classifiedId: Uuid, emailAddress: String, token: String): Result<Unit>
    suspend fun processClassifiedPayment(payment: ClassifiedPayment): Result<ClassifiedPaymentResponse>
}
