package com.green.yp.app.shared.dto.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequestParams(
    val zipCode: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val keywords: String? = null,
    val categoryRefId: String? = null,
    val distance: Int? = null
)
