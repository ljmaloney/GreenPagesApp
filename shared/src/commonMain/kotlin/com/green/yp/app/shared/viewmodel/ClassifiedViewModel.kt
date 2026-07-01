package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import com.green.yp.app.shared.repository.ClassifiedRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClassifiedViewModel(
    private val repository: ClassifiedRepository
) : ViewModel() {

    val categories: StateFlow<List<ClassifiedCategory>> = repository.categories
    val adTypes: StateFlow<List<ClassifiedAdType>> = repository.adTypes
    val errorMessage: StateFlow<String?> = repository.errorMessage

    init {
        fetchCategories()
        fetchAdTypes()
    }

    fun fetchCategories() {
        println("ClassifiedViewModel: fetchCategories called")
        viewModelScope.launch {
            repository.getCategories()
        }
    }

    fun fetchAdTypes() {
        println("ClassifiedViewModel: fetchAdTypes called")
        viewModelScope.launch {
            repository.getClassifiedAdTypes()
        }
    }

    fun retry() {
        fetchCategories()
        fetchAdTypes()
    }
}