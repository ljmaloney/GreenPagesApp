package com.green.yp.app.shared.dto.reference

import kotlin.uuid.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineOfBusiness(
    @SerialName("lineOfBusinessId")
    val lineOfBusinessId: Uuid,
    @SerialName("createDate")
    val createDate: String,
    @SerialName("lastUpdateDate")
    val lastUpdateDate: String,
    @SerialName("lineOfBusinessName")
    val lineOfBusinessName: String,
    @SerialName("urlLob")
    val urlLob: String,
    @SerialName("createType")
    val createType: String,
    @SerialName("createByReference")
    val createByReference: String,
    @SerialName("shortDescription")
    val shortDescription: String,
    @SerialName("description")
    val description: String,
    @SerialName("enableDistanceRadius")
    val enableDistanceRadius: Boolean,
    @SerialName("iconName")
    val iconName: String,
    @SerialName("iconFileName")
    val iconFileName: String? = null
)