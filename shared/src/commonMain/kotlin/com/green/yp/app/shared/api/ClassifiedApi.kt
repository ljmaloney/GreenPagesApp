package com.green.yp.app.shared.api

import com.green.yp.app.shared.dto.ResponseWrapper
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ImageGallery
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent
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

    @GET("classified/{classifiedId}")
    suspend fun getClassified(@Path(value="classifiedId") classifiedId: Uuid): ResponseWrapper<ClassifiedResponse>

    @GET("classified/{classifiedId}/image/gallery")
    suspend fun getClassifiedImageGallery(@Path("classifiedId") classifiedId: Uuid): ResponseWrapper<List<ImageGallery>>

    @Multipart
    @POST("classified/{classifiedId}/image/gallery")
    suspend fun uploadImage(
        @Path("classifiedId") classifiedId: Uuid,
        @Body file: MultiPartFormDataContent,
        @Query("imageFilename") imageFilename: String,
        @Query("imageDescription") imageDescription: String?
    ): ResponseWrapper<Unit?>
}
