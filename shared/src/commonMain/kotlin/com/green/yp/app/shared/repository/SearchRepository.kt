package com.green.yp.app.shared.repository

import com.green.yp.app.shared.dto.PageableResponse
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {
    val searchResults: StateFlow<PageableResponse<SearchResponseDTO>?>
    val errorMessage: StateFlow<String?>
    
    suspend fun search(
        zipCode: String? = null,
        keywords: String? = null,
        categoryRefId: String? = null,
        distance: Int? = null,
        page: Int? = 0,
        limit: Int? = 15
    ): Result<PageableResponse<SearchResponseDTO>>

    suspend fun search(
        latitude: Double? = null,
        longitude: Double? = null,
        keywords: String? = null,
        categoryRefId: String? = null,
        distance: Int? = null,
        page: Int? = 0,
        limit: Int? = 15
    ): Result<PageableResponse<SearchResponseDTO>>
}
