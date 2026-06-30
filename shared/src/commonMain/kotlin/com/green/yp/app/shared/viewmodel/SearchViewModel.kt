package com.green.yp.app.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<SearchResponseDTO>>(emptyList())
    val searchResults: StateFlow<List<SearchResponseDTO>> = _searchResults.asStateFlow()

    private val _isRefreshing = MutableStateFlow(value = false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(value = false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    val errorMessage: StateFlow<String?> = repository.errorMessage

    private var currentPage = 0
    private var totalPages = 0
    private var lastParams: SearchParams? = null

    private data class SearchParams(
        val zipCode: String?,
        val latitude: Double?,
        val longitude: Double?,
        val keywords: String?,
        val categoryRefId: String?,
        val distance: Int?
    )

    fun search(
        zipCode: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        keywords: String? = null,
        categoryRefId: String? = null,
        distance: Int? = null
    ) {
        val newParams = SearchParams(zipCode, latitude, longitude, keywords, categoryRefId, distance)
        if (newParams == lastParams && _isRefreshing.value) return
        
        lastParams = newParams
        currentPage = 0
        _searchResults.value = emptyList()
        fetchPage(isInitial = true)
    }

    fun loadMore() {
        if (_isLoadingMore.value || (currentPage >= totalPages - 1)) return
        currentPage++
        fetchPage(isInitial = false)
    }

    private fun fetchPage(isInitial: Boolean) {
        val params = lastParams ?: return
        
        viewModelScope.launch {
            if (isInitial) _isRefreshing.value = true else _isLoadingMore.value = true
            
            val result = if ((params.latitude != null) && (params.longitude != null)) {
                repository.search(
                    latitude = params.latitude,
                    longitude = params.longitude,
                    keywords = params.keywords,
                    categoryRefId = params.categoryRefId,
                    distance = params.distance,
                    page = currentPage
                )
            } else {
                repository.search(
                    zipCode = params.zipCode,
                    keywords = params.keywords,
                    categoryRefId = params.categoryRefId,
                    distance = params.distance,
                    page = currentPage
                )
            }

            result.onSuccess { response ->
                totalPages = response.totalPages
                _searchResults.value = if (isInitial) {
                    response.pageableResults
                } else {
                    _searchResults.value + response.pageableResults
                }
            }
            
            if (isInitial) _isRefreshing.value = false else _isLoadingMore.value = false
        }
    }
}
