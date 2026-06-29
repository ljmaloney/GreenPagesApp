package com.green.yp.app.shared.api

import com.green.yp.app.shared.dto.PageableResponse
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface SearchApi {

    @GET("v2/search")
    suspend fun search(
        @Query("zipCode") zipCode: String? = null,
        @Query("keywords") keywords: String? = null,
        @Query("categoryRefId") categoryRefId: String? = null,
        @Query("distance") distance: Int? = null,
        @Query("page") page: Int? = 0,
        @Query("limit") limit: Int? = 15
    ): PageableResponse<SearchResponseDTO>

    @GET("v2/search/nearme")
    suspend fun search(
        @Query("latitude") latitude: Double? = null,
        @Query("latitude") longitude: Double? = null,
        @Query("keywords") keywords: String? = null,
        @Query("categoryRefId") categoryRefId: String? = null,
        @Query("distance") distance: Int? = null,
        @Query("page") page: Int? = 0,
        @Query("limit") limit: Int? = 15
    ): PageableResponse<SearchResponseDTO>
}
