package com.green.yp.app.shared.api

import com.green.yp.app.shared.dto.ResponseWrapper
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface ClassifiedApi {
    @POST("classified/create-ad")
    suspend fun createClassifiedAd(@Body request: ClassifiedRequest): ResponseWrapper<ClassifiedResponse>

    @POST("classified/{classifiedId}/validate")
    suspend fun validateClassifiedEmail(
        @Path("classifiedId") classifiedId: Uuid,
        @Query("emailAddress") emailAddress: String,
        @Query("token") token: String
    ): ResponseWrapper<Unit?>

    @POST("classified/payment")
    suspend fun processClassifiedPayment(@Body request: ClassifiedPayment): ResponseWrapper<ClassifiedPaymentResponse>
}
