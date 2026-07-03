package com.green.yp.app.media

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class AndroidImagePicker(
    private val activity: ComponentActivity
) : ImagePicker {

    private var callback: ((ImageResult) -> Unit)? = null

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                callback?.invoke(it.toImageResult(activity.contentResolver))
            }
        }

    override fun pickImage(onResult: (ImageResult) -> Unit) {
        callback = onResult
        launcher.launch("image/*")
    }

    private fun Uri.toImageResult(contentResolver: ContentResolver): ImageResult {

        val bytes = contentResolver.openInputStream(this)?.use { input ->
            input.buffered().readBytes()
        } ?: throw IllegalStateException("Failed to read image")

        val fileName = runCatching {
            contentResolver.query(
                this,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(index)
            }
        }.getOrNull() ?: "image.jpg"

        val contentType = contentResolver.getType(this) ?: "image/jpeg"

        return ImageResult(
            bytes = bytes,
            fileName = fileName,
            contentType = contentType
        )
    }
}