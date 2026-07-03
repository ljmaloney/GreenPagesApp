package com.green.yp.app.media

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.green.yp.app.ui.theme.DarkGreen

@Composable
fun ImagePickerField(
    picker: ImagePicker,
    onSelected: (ImageResult) -> Unit
) {

    var selected by remember { mutableStateOf<ImageResult?>(null) }

    Column {

        Button(
            onClick = {
                picker.pickImage { result ->
                    selected = result
                    onSelected(result)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            )
        ) {
            Text("Select Image")
        }

        selected?.let {
            Text("Selected: ${it.fileName}")
        }
    }
}