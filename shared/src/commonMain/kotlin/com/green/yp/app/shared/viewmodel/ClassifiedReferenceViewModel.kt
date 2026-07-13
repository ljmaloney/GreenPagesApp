package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import com.green.yp.app.shared.repository.ClassifiedReferenceRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ClassifiedReferenceViewModel(
    private val repository: ClassifiedReferenceRepository
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

    @OptIn(ExperimentalUuidApi::class)
    fun getAdTypeById(adTypeId: Uuid): ClassifiedAdType? {
        return adTypes.value.find { it.adTypeId == adTypeId }
    }
}