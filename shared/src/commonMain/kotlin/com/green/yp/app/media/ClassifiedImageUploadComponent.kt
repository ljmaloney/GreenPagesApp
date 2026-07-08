package com.green.yp.app.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.dto.classified.ClassifiedImageUpload
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.ui.theme.DarkGreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ClassifiedImageUploadComponent(
    classifiedId: Uuid,
    viewModel: ClassifiedViewModel,
    imagePicker: ImagePicker
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    ClassifiedImageUploadComponentContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        imagePicker = imagePicker,
        onUpload = { image, fileName, description ->
            viewModel.uploadImage(
                ClassifiedImageUpload(
                    classifiedId = classifiedId,
                    bytes = image.bytes,
                    fileName = fileName,
                    contentType = image.contentType,
                    description = description
                )
            )
        }
    )
}

@Composable
fun ClassifiedImageUploadComponentContent(
    isLoading: Boolean,
    errorMessage: String?,
    imagePicker: ImagePicker,
    initialImage: ImageResult? = null,
    onUpload: (ImageResult, String, String) -> Unit
) {
    var selectedImage by remember { mutableStateOf(initialImage) }
    var fileName by remember { mutableStateOf(initialImage?.fileName ?: "") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Upload Image",
            style = MaterialTheme.typography.titleMedium,
            color = DarkGreen,
            fontWeight = FontWeight.Bold
        )

        ImagePickerField(
            picker = imagePicker,
            onSelected = { result ->
                selectedImage = result
                fileName = result.fileName
            }
        )

        selectedImage?.let { image ->
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                ImageThumbnail(image = image)
            }

            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = { Text("File Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    selectedImage?.let {
                        onUpload(it, fileName, description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && selectedImage != null && fileName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Upload Image")
                }
            }
        }
    }
}

@Preview
@Composable
fun ClassifiedImageUploadComponentPreview() {
    val mockImagePicker = object : ImagePicker {
        override fun pickImage(onResult: (ImageResult) -> Unit) {
        }
    }

    MaterialTheme {
        Surface {
            ClassifiedImageUploadComponentContent(
                isLoading = false,
                errorMessage = null,
                imagePicker = mockImagePicker,
                initialImage = ImageResult(ByteArray(0), "preview_image.jpg", "image/jpeg"),
                onUpload = { _, _, _ -> }
            )
        }
    }
}

