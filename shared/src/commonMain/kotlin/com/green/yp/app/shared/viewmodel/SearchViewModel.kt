package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.dto.PageableResponse
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.repository.SearchRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    val searchResults: StateFlow<PageableResponse<SearchResponseDTO>?> = repository.searchResults
    val errorMessage: StateFlow<String?> = repository.errorMessage

    fun search(
        zipCode: String? = null,
        keywords: String? = null,
        categoryRefId: String? = null,
        distance: Int? = null,
        page: Int? = 0,
        limit: Int? = 15
    ) {
        viewModelScope.launch {
            repository.search(
                zipCode = zipCode,
                keywords = keywords,
                categoryRefId = categoryRefId,
                distance = distance,
                page = page,
                limit = limit
            )
        }
    }
}
