package com.green.yp.app.shared.repository

import com.green.yp.app.shared.dto.classified.ClassifiedCategory

interface ClassifiedRepository {
    suspend fun getCategories(): Result<List<ClassifiedCategory>>
}