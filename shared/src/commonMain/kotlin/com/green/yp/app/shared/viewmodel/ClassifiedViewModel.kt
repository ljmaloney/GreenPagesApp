package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ImageGallery
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import com.green.yp.app.shared.dto.classified.ClassifiedImageUpload
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
    val imageGallery: StateFlow<List<ImageGallery>> = repository.imageGallery

    fun createClassifiedAd(request: ClassifiedRequest) {
        viewModelScope.launch {
            repository.createClassifiedAd(request)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun validateClassifiedEmail(classifiedId: Uuid, emailAddress: String, token: String) {
        viewModelScope.launch {
            repository.validateClassifiedEmail(classifiedId, emailAddress, token)
        }
    }

    fun processClassifiedPayment(payment: ClassifiedPayment) {
        viewModelScope.launch {
            repository.processClassifiedPayment(payment)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getClassifiedImages(classifiedId: Uuid) {
        viewModelScope.launch {
            repository.getClassifiedImageGallery(classifiedId)
        }
    }

    fun uploadImage(request: ClassifiedImageUpload) {
        viewModelScope.launch {
            repository.uploadImage(request)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getClassified(classifiedId: Uuid) {
        viewModelScope.launch {
            repository.getClassified(classifiedId)
        }
    }
}
