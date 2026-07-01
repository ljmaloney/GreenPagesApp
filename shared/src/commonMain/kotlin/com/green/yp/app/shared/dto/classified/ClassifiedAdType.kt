package com.green.yp.app.shared.dto.classified

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ClassifiedAdType(
    @SerialName("adTypeId")
    val adTypeId: Uuid,
    @SerialName("createDate")
    val createDate: String,
    @SerialName("active")
    val active: Boolean,
    @SerialName("defaultPackage")
    val defaultPackage: Boolean,
    @SerialName("adTypeName")
    val adTypeName: String,
    @SerialName("monthlyPrice")
    val monthlyPrice: Double,
    @SerialName("threeMonthPrice")
    val threeMonthPrice: Double,
    @SerialName("features")
    val features: ClassifiedAdFeatures
)

@Serializable
data class ClassifiedAdFeatures(
    @SerialName("features")
    val features: List<String>,
    @SerialName("maxImages")
    val maxImages: Int,
    @SerialName("protectContact")
    val protectContact: Boolean
)
