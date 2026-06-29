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
        latitude: Double? = null,
        longitude: Double? = null,
        keywords: String? = null,
        categoryRefId: String? = null,
        distance: Int? = null,
        page: Int? = 0,
        limit: Int? = 15
    ) {
        if ( latitude != null && longitude != null){
            viewModelScope.launch {
                repository.search(
                    latitude = latitude,
                    longitude = longitude,
                    keywords = keywords,
                    categoryRefId = categoryRefId,
                    distance = distance,
                    page = page,
                    limit = limit
                )
            }
        } else {
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
}
