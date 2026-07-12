package com.green.yp.app.media

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

class AndroidImagePicker(
    private val activity: ComponentActivity
) : ImagePicker {

    private var callback: ((ImageResult) -> Unit)? = null

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            Log.d("AndroidImagePicker", "Picker result received: $uri")
            uri?.let {
                try {
                    callback?.invoke(it.toImageResult(activity.contentResolver))
                } catch (e: Exception) {
                    Log.e("AndroidImagePicker", "Error processing image", e)
                }
            }
        }

    override fun pickImage(onResult: (ImageResult) -> Unit) {
        Log.d("AndroidImagePicker", "pickImage called")
        callback = onResult
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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