package com.green.yp.app.shared.repository

import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.ktor.client.plugins.*

class ClassifiedRepositoryImpl(
    private val classifiedApi: ClassifiedApi) : ClassifiedRepository {

    private val _categories = MutableStateFlow<List<ClassifiedCategory>>(emptyList())
    override val categories: StateFlow<List<ClassifiedCategory>> = _categories.asStateFlow()

    private val _adTypes = MutableStateFlow<List<ClassifiedAdType>>(emptyList())
    override val adTypes: StateFlow<List<ClassifiedAdType>> = _adTypes.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    override suspend fun getCategories():
            Result<List<ClassifiedCategory>> {

        println("ClassifiedRepositoryImpl: getCategories called")
        return runCatching {

            val result = classifiedApi.getCategories()
            println("ClassifiedRepositoryImpl: API response received: $result")

            result.errorMessageApi?.let { error ->
                println("ClassifiedRepositoryImpl: API error: ${error.displayMessage}")
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }
            val data = result.response
            println("ClassifiedRepositoryImpl: Categories count: ${data.size}")
            _categories.value = data
            // Clear any previous error on success
            _errorMessage.value = null
            data
        }.onFailure { throwable ->
            // Map common Ktor HTTP exceptions to user-facing messages
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            println("ClassifiedRepositoryImpl: getCategories failed with: ${throwable.message}")
            _errorMessage.value = message
        }
    }

    override suspend fun getClassifiedAdTypes(): Result<List<ClassifiedAdType>> {
        println("ClassifiedRepositoryImpl: getClassifiedAdTypes called")
        return runCatching {
            val result = classifiedApi.getClassifiedAdTypes()
            println("ClassifiedRepositoryImpl: API response received: $result")

            result.errorMessageApi?.let { error ->
                println("ClassifiedRepositoryImpl: API error: ${error.displayMessage}")
                _errorMessage.value = error.displayMessage
                throw IllegalStateException(error.displayMessage)
            }
            val data = result.response
            println("ClassifiedRepositoryImpl: Ad Types count: ${data.size}")
            _adTypes.value = data
            _errorMessage.value = null
            data
        }.onFailure { throwable ->
            val message = when (throwable) {
                is ClientRequestException -> "Client error: ${throwable.response.status.value}"
                is ServerResponseException -> "Server error: ${throwable.response.status.value}"
                is ResponseException -> "Network error: ${throwable.response.status.value}"
                else -> throwable.message ?: "Unknown error"
            }
            println("ClassifiedRepositoryImpl: getClassifiedAdTypes failed with: ${throwable.message}")
            _errorMessage.value = message
        }
    }
}