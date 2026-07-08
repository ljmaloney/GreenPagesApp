package com.green.yp.app.media

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                println("DEBUG: Select Image button clicked")
                picker.pickImage { result ->
                    println("DEBUG: Image picked: ${result.fileName}")
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