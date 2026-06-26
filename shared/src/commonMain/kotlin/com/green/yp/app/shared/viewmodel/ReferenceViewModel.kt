package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.dto.reference.LineOfBusiness
import com.green.yp.app.shared.repository.ReferenceRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReferenceViewModel(
    private val repository: ReferenceRepository
) : ViewModel() {

    val linesOfBusiness: StateFlow<List<LineOfBusiness>> = repository.linesOfBusiness
    val errorMessage: StateFlow<String?> = repository.errorMessage

    init {
        fetchLinesOfBusiness()
    }

    fun fetchLinesOfBusiness() {
        viewModelScope.launch {
            repository.getLinesOfBusiness()
        }
    }

    fun retry() {
        fetchLinesOfBusiness()
    }
}
