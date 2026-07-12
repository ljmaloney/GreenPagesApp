package com.green.yp.app.shared.dto.classified

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ImageGallery( @SerialName("imageName") val imageName: String,
                         @SerialName("description")val description: String?,
                         @SerialName("url") val url: String)
