package com.green.yp.app.shared.api

import com.green.yp.app.shared.dto.ResponseWrapper
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import de.jensklingenberg.ktorfit.http.GET

interface ClassifiedApi {

    @GET("reference/classified/categories")
    suspend fun getCategories(): ResponseWrapper<List<ClassifiedCategory>>
}