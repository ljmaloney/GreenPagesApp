package com.green.yp.app.media

import androidx.compose.runtime.staticCompositionLocalOf

interface ImagePicker {
    fun pickImage(onResult: (ImageResult) -> Unit)
}

val LocalImagePicker = staticCompositionLocalOf<ImagePicker?> { null }

data class ImageResult(
    val bytes: ByteArray,
    val fileName: String,
    val contentType: String
)