package com.green.yp.app.field

import android.content.ContentResolver
import android.net.Uri
import com.green.yp.app.shared.dto.classified.ClassifiedImageUpload
import kotlin.uuid.Uuid

object ImageUploadMapper {

    fun toUpload(
        classifiedId: Uuid,
        uri: Uri,
        resolver: ContentResolver
    ): ClassifiedImageUpload {

        val bytes = ImageCompression.uriToJpegBytes(resolver, uri)

        return ClassifiedImageUpload(
            classifiedId = classifiedId,
            fileName = "img_${System.currentTimeMillis()}.jpg",
            description = null,
            bytes = bytes,
            contentType = "image/jpeg"
        )
    }
}