package com.green.yp.app.shared.dto.classified

import kotlin.uuid.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassifiedCategory(
    @SerialName("categoryId")
    val categoryId: Uuid,
    @SerialName("active")
    val active: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("urlName")
    val urlName: String,
    @SerialName("shortDescription")
    val shortDescription: String? = null,
    @SerialName("description")
    val description: String? = null
)