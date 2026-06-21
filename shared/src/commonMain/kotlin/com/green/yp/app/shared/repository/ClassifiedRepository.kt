package com.green.yp.app.shared.repository

import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import kotlinx.coroutines.flow.StateFlow

interface ClassifiedRepository {
    val categories: StateFlow<List<ClassifiedCategory>>
    // Expose an error message to be observed by the UI. Null when no error.
    val errorMessage: StateFlow<String?>
    suspend fun getCategories(): Result<List<ClassifiedCategory>>
}