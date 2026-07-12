package com.green.yp.app.media

interface IOSPickerDelegate {
    fun pickImage(callback: (ByteArray, String) -> Unit)
}

class IOSImagePicker(private val bridge: IOSPickerDelegate) : ImagePicker {

    private var callback: ((ImageResult) -> Unit)? = null

    override fun pickImage(onResult: (ImageResult) -> Unit) {
        callback = onResult

        bridge.pickImage { data, fileName ->
            callback?.invoke(
                ImageResult(
                    bytes = data,
                    fileName = fileName,
                    contentType = "image/jpeg"
                )
            )
        }
    }
}
