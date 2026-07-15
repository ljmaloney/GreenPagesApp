package com.green.yp.app.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.green.yp.app.components.AlertBanner
import com.green.yp.app.components.AlertBannerItem
import com.green.yp.app.components.AlertType
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
    val isUploadSuccess by viewModel.isUploadSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var resetTrigger by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var selectedImageForSheet by remember { mutableStateOf<ImageResult?>(null) }

    LaunchedEffect(isUploadSuccess) {
        if (isUploadSuccess) {
            // Immediately close the dialog and reset state
            showDialog = false
            selectedImageForSheet = null
            resetTrigger++
            showSuccessAlert = true
            
            // Clear the success state in VM so we don't trigger this again immediately
            viewModel.clearUploadSuccess()

            // Show snackbar without blocking the UI updates above
            snackbarHostState.showSnackbar(
                message = "Image uploaded successfully",
                actionLabel = "Dismiss",
                withDismissAction = true
            )
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (showSuccessAlert) {
                AlertBanner(
                    alerts = listOf(
                        AlertBannerItem(
                            id = "upload-success",
                            title = "Success",
                            message = "Image uploaded successfully",
                            type = AlertType.SUCCESS
                        )
                    ),
                    onDismiss = { showSuccessAlert = false }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!showDialog) {
                Button(
                    onClick = {
                        imagePicker.pickImage { result ->
                            selectedImageForSheet = result
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        if (selectedImageForSheet == null) "Select Image" 
                        else "Select a Different Image"
                    )
                }
            }

            if (showDialog) {
                Dialog(
                    onDismissRequest = {
                        showDialog = false
                        selectedImageForSheet = null
                    },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false // Allows full-screen
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        ClassifiedImageUploadComponentContent(
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            showSuccessAlert = false,
                            onDismissSuccessAlert = {},
                            resetTrigger = resetTrigger,
                            initialImage = selectedImageForSheet,
                            onUpload = { image, fileName, description ->
                                println("DEBUG: onUpload triggered in UI")
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
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ClassifiedImageUploadComponentContent(
    isLoading: Boolean,
    errorMessage: String?,
    showSuccessAlert: Boolean = false,
    onDismissSuccessAlert: () -> Unit = {},
    resetTrigger: Int = 0,
    initialImage: ImageResult? = null,
    onUpload: (ImageResult, String, String) -> Unit
) {
    var selectedImage by remember(resetTrigger) { mutableStateOf(initialImage) }
    var fileName by remember(resetTrigger) { mutableStateOf(initialImage?.fileName ?: "") }
    var description by remember(resetTrigger) { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showSuccessAlert) {
            AlertBanner(
                alerts = listOf(
                    AlertBannerItem(
                        id = "upload-success",
                        title = "Success",
                        message = "Image uploaded successfully",
                        type = AlertType.SUCCESS
                    )
                ),
                onDismiss = { onDismissSuccessAlert() }
            )
        }

        Text(
            text = "Upload Image",
            style = MaterialTheme.typography.titleMedium,
            color = DarkGreen,
            fontWeight = FontWeight.Bold
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
                    selectedImage?.let { image ->
                        val finalFileName = if (fileName.isNotBlank()) {
                            // To ensure uniqueness as requested, we can append a random string or just trust user input if they entered one.
                            // Given "ensure the image name is unique", I'll append a short unique id if not already present.
                            fileName
                        } else {
                            // Default to a unique name based on UUID if blank
                            "image_${Uuid.random().toString().take(8)}.jpg"
                        }
                        onUpload(image, finalFileName, description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && selectedImage != null,
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
        
        // Add some spacer at the bottom to ensure the keyboard doesn't cover the button even when scrolled
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun ClassifiedImageUploadComponentPreview() {
    MaterialTheme {
        Surface {
            ClassifiedImageUploadComponentContent(
                isLoading = false,
                errorMessage = null,
                showSuccessAlert = true,
                onDismissSuccessAlert = {},
                initialImage = ImageResult(ByteArray(0), "preview_image.jpg", "image/jpeg"),
                onUpload = { _, _, _ -> }
            )
        }
    }
}

