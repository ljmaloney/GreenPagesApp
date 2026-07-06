package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.repository.EmailContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmailContactViewModel(
    private val repository: EmailContactRepository
) : ViewModel() {

    val errorMessage: StateFlow<String?> = repository.errorMessage
    val isLoading: StateFlow<Boolean> = repository.isLoading

    private val _isValidated = MutableStateFlow(false)
    val isValidated: StateFlow<Boolean> = _isValidated.asStateFlow()

    fun validateEmail(externRef: String, emailAddress: String, token: String) {
        viewModelScope.launch {
            val result = repository.validateEmail(externRef, emailAddress, token)
            _isValidated.value = result.isSuccess
        }
    }
    
    fun resetValidationState() {
        _isValidated.value = false
    }
}
