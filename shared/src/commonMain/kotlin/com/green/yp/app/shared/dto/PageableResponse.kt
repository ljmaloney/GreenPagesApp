package com.green.yp.app.shared.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageableResponse<T>(
    @SerialName("pageableResults")
    val pageableResults: List<T>,
    @SerialName("totalCount")
    val totalCount: Int,
    @SerialName("currentPage")
    val currentPage: Int,
    @SerialName("totalPages")
    val totalPages: Int
)
