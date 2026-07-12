package com.green.yp.app.field

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class ImagePickerController(
    activity: ComponentActivity,
    private val onImageSelected: (Uri) -> Unit
) {

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let(onImageSelected)
        }

    fun pickImage() {
        launcher.launch("image/*")
    }
}