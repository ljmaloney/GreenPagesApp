package com.green.yp.app.components.classified

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.green.yp.app.media.ClassifiedImageUploadComponent
import com.green.yp.app.media.ImagePicker
import com.green.yp.app.shared.dto.classified.ImageGallery
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.ui.theme.DarkGreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import androidx.compose.ui.tooling.preview.Preview
import com.green.yp.app.media.ImageResult

@OptIn(ExperimentalUuidApi::class)
@Composable
fun UploadImages(
    classifiedId: Uuid,
    maxImages: Int,
    viewModel: ClassifiedViewModel,
    imagePicker: ImagePicker,
    modifier: Modifier = Modifier
) {
    val imageGallery by viewModel.imageGallery.collectAsState()

    // Refresh gallery when component is loaded or after an upload might have happened
    LaunchedEffect(classifiedId) {
        viewModel.getClassifiedImages(classifiedId)
    }

    UploadImagesContent(
        classifiedId = classifiedId,
        maxImages = maxImages,
        imageGallery = imageGallery,
        viewModel = viewModel,
        imagePicker = imagePicker,
        modifier = modifier
    )
}

@Composable
fun UploadImagesContent(
    classifiedId: Uuid,
    maxImages: Int,
    imageGallery: List<ImageGallery>,
    viewModel: ClassifiedViewModel,
    imagePicker: ImagePicker,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "4. Upload Images",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkGreen
        )

        Text(
            text = "You can upload up to $maxImages images for your ad. (${imageGallery.size}/$maxImages uploaded)",
            style = MaterialTheme.typography.bodyMedium
        )

        if (imageGallery.size < maxImages) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                ClassifiedImageUploadComponent(
                    classifiedId = classifiedId,
                    viewModel = viewModel,
                    imagePicker = imagePicker
                )
            }
        } else {
            InfoBanner(text = "You have reached the maximum number of images for this ad type.")
        }

        if (imageGallery.isNotEmpty()) {
            Text(
                text = "Uploaded Images",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    items(imageGallery) { image ->
                        ImageGalleryItem(image = image)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageGalleryItem(image: ImageGallery) {
    Column {
        AsyncImage(
            model = image.url,
            contentDescription = image.description,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = image.imageName,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun InfoBanner(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
fun UploadImagesPreview() {
    val mockImagePicker = object : ImagePicker {
        override fun pickImage(onResult: (ImageResult) -> Unit) {}
    }
    
    // Note: Since I can't easily mock the ViewModel's StateFlows in a preview without a factory or interface,
    // in a real scenario I'd refactor to a stateless content component.
    // For now, I'll just show the header and a placeholder to avoid compilation errors if ViewModel needs real deps.
    
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("4. Upload Images", style = MaterialTheme.typography.headlineSmall, color = DarkGreen)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Preview requires a ViewModel instance. In actual use, this component shows the upload form and gallery.")
        }
    }
}
