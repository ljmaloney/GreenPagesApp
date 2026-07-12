package com.green.yp.app.shared.dto.classified

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ClassifiedRequest(
    @SerialName("adType")
    val adType: Uuid,
    @SerialName("categoryId")
    val categoryId: Uuid,
    @SerialName("price")
    val price: Double,
    @SerialName("pricePerUnitType")
    val pricePerUnitType: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("address")
    val address: String,
    @SerialName("city")
    val city: String,
    @SerialName("state")
    val state: String,
    @SerialName("postalCode")
    val postalCode: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("emailAddress")
    val emailAddress: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String
)
