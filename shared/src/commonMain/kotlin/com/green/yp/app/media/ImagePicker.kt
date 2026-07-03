package com.green.yp.app.media

interface ImagePicker {
    fun pickImage(onResult: (ImageResult) -> Unit)
}

data class ImageResult(
    val bytes: ByteArray,
    val fileName: String,
    val contentType: String
)