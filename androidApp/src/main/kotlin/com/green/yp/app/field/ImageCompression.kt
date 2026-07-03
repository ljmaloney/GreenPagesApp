package com.green.yp.app.field

import android.provider.MediaStore
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream

object ImageCompression {

    fun uriToJpegBytes(
        resolver: ContentResolver,
        uri: Uri,
        quality: Int = 80,
        maxWidth: Int = 1600
    ): ByteArray {

        val bitmap = if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(resolver, uri)
            )
        } else {
            MediaStore.Images.Media.getBitmap(resolver, uri)
        }

        val scaled = if (bitmap.width > maxWidth) {
            val ratio = maxWidth.toFloat() / bitmap.width
            Bitmap.createScaledBitmap(
                bitmap,
                maxWidth,
                (bitmap.height * ratio).toInt(),
                true
            )
        } else bitmap

        val stream = ByteArrayOutputStream()

        scaled.compress(
            Bitmap.CompressFormat.JPEG,
            quality,
            stream
        )

        return stream.toByteArray()
    }
}