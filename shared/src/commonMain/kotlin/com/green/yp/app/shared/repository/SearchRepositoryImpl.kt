package com.green.yp.app.shared.repository

import com.green.yp.app.shared.api.SearchApi
import com.green.yp.app.shared.dto.PageableResponse
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.ktor.client.plugins.*

class SearchRepositoryImpl(
    private val searchApi: SearchApi
) : SearchRepository {

    private val _searchResults = MutableStateFlow<PageableResponse<SearchResponseDTO>?>(null)
    override val searchResults: StateFlow<PageableResponse<SearchResponseDTO>?> = _searchResults.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    override suspend fun search(
        zipCode: String?,
        keywords: String?,
        categoryRefId: String?,
        distance: Int?,
        page: Int?,
        limit: Int?
    ): Result<PageableResponse<SearchResponseDTO>> {
        println("SearchRepositoryImpl: search called with zipCode=$zipCode, keywords=$keywords")
        return runCatching {
            val result = searchApi.search(
                zipCode = zipCode,
                keywords = keywords,
                categoryRefId = categoryRefId,
                distance = distance,
                page = page,
                limit = limit
            )
            println("SearchRepositoryImpl: API response received, totalCount=${result.totalCount}")
            _searchResults.value = result
            _errorMessage.value = null
            result
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            println("SearchRepositoryImpl: search failed with: $message")
            _errorMessage.value = message
        }
    }

    override suspend fun search(
        latitude: Double?,
        longitude: Double?,
        keywords: String?,
        categoryRefId: String?,
        distance: Int?,
        page: Int?,
        limit: Int?
    ): Result<PageableResponse<SearchResponseDTO>> {
        println("SearchRepositoryImpl: search called with latitude=$latitude, longitude=$longitude, keywords=$keywords")
        return runCatching {
            val result = searchApi.search(
                latitude = latitude,
                longitude = longitude,
                keywords = keywords,
                categoryRefId = categoryRefId,
                distance = distance,
                page = page,
                limit = limit
            )
            println("SearchRepositoryImpl: API response received, totalCount=${result.totalCount}")
            _searchResults.value = result
            _errorMessage.value = null
            result
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            println("SearchRepositoryImpl: search failed with: $message")
            _errorMessage.value = message
        }
    }
}
