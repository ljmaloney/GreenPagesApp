package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import com.green.yp.app.shared.repository.ClassifiedRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ClassifiedViewModel(private val repository: ClassifiedRepository) : ViewModel() {

    val createdAd: StateFlow<ClassifiedResponse?> = repository.createdAd
    val paymentResponse: StateFlow<ClassifiedPaymentResponse?> = repository.paymentResponse
    val errorMessage: StateFlow<String?> = repository.errorMessage
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val isValidated: StateFlow<Boolean> = repository.isValidated

    fun createClassifiedAd(request: ClassifiedRequest) {
        viewModelScope.launch {
            repository.createClassifiedAd(request)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun validateClassifiedEmail(classifiedId: String, emailAddress: String, token: String) {
        viewModelScope.launch {
            repository.validateClassifiedEmail(Uuid.parse(classifiedId), emailAddress, token)
        }
    }

    fun processClassifiedPayment(payment: ClassifiedPayment) {
        viewModelScope.launch {
            repository.processClassifiedPayment(payment)
        }
    }
}
