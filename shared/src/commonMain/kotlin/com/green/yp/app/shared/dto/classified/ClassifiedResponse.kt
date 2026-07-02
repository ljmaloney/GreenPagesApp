package com.green.yp.app.shared.dto.classified

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ClassifiedResponse(
    @SerialName("classifiedId")
    val classifiedId: Uuid,
    @SerialName("createDate")
    val createDate: String,
    @SerialName("lastUpdateDate")
    val lastUpdateDate: String,
    @SerialName("activeDate")
    val activeDate: String,
    @SerialName("lastActiveDate")
    val lastActiveDate: String,
    @SerialName("renewalCount")
    val renewalCount: Int,
    @SerialName("categoryId")
    val categoryId: Uuid,
    @SerialName("adTypeId")
    val adTypeId: Uuid,
    @SerialName("price")
    val price: Double,
    @SerialName("perUnitType")
    val perUnitType: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("city")
    val city: String,
    @SerialName("state")
    val state: String,
    @SerialName("postalCode")
    val postalCode: String,
    @SerialName("emailAddress")
    val emailAddress: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("latitude")
    val latitude: Double
)
