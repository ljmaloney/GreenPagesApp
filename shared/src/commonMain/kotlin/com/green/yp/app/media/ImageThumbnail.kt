package com.green.yp.app.media

import androidx.compose.runtime.Composable

@Composable
fun ImageThumbnail(
    image: ImageResult?
) {
    if (image == null) return

    val bitmap = image.bytes.toImageBitmap()

    bitmap?.let {
        androidx.compose.foundation.Image(
            bitmap = it,
            contentDescription = null
        )
    }
}