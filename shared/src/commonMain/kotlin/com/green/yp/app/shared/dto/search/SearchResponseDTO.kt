package com.green.yp.app.shared.dto.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDTO(
    @SerialName("externId")
    val externId: String,
    @SerialName("producerId")
    val producerId: String,
    @SerialName("locationId")
    val locationId: String,
    @SerialName("categoryRef")
    val categoryRef: String,
    @SerialName("categoryName")
    val categoryName: String? = null,
    @SerialName("recordType")
    val recordType: SearchRecordType,
    @SerialName("active")
    val active: Boolean,
    @SerialName("lastActiveDate")
    val lastActiveDate: String? = null,
    @SerialName("keywords")
    val keywords: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("businessName")
    val businessName: String,
    @SerialName("businessUrl")
    val businessUrl: String? = null,
    @SerialName("businessIconUrl")
    val businessIconUrl: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("addressLine1")
    val addressLine1: String,
    @SerialName("addressLine2")
    val addressLine2: String? = null,
    @SerialName("city")
    val city: String,
    @SerialName("state")
    val state: String,
    @SerialName("postalCode")
    val postalCode: String,
    @SerialName("emailAddress")
    val emailAddress: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("minPrice")
    val minPrice: Double? = null,
    @SerialName("maxPrice")
    val maxPrice: Double? = null,
    @SerialName("priceUnitsType")
    val priceUnitsType: String? = null,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("distance")
    val distance: Double,
    @SerialName("description")
    val description: String? = null
)
