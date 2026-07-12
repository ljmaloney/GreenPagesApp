package com.green.yp.app.shared.dto.classified

import kotlin.uuid.Uuid

data class ClassifiedImageUpload(
    val classifiedId: Uuid,
    val fileName: String,
    val description: String?,
    val bytes: ByteArray,
    val contentType: String
)
