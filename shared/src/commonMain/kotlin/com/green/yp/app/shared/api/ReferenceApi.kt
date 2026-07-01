package com.green.yp.app.shared.api

import com.green.yp.app.shared.dto.ResponseWrapper
import com.green.yp.app.shared.dto.reference.LineOfBusiness
import de.jensklingenberg.ktorfit.http.GET

interface ReferenceApi {

    @GET("reference/lob")
    suspend fun getLinesOfBusiness(): ResponseWrapper<List<LineOfBusiness>>
}
