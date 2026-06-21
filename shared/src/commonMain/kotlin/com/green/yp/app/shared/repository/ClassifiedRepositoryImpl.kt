package com.green.yp.app.shared.repository

import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import com.green.yp.app.shared.repository.ClassifiedRepository

class ClassifiedRepositoryImpl(
    private val classifiedApi: ClassifiedApi) : ClassifiedRepository {

    override suspend fun getCategories():
            Result<List<ClassifiedCategory>> {

        return runCatching {

            val result = classifiedApi.getCategories()

            result.errorMessageApi?.let { error ->
                throw IllegalStateException(error.displayMessage)
            }
            result.response
        }
    }
}