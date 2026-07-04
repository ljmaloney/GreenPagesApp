package com.green.yp.app.wizard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.dto.classified.ImageGallery
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.ui.theme.DarkGreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ClassifiedPreview(
    classifiedId: Uuid,
    viewModel: ClassifiedViewModel,
    modifier: Modifier = Modifier
) {
    val classified by viewModel.createdAd.collectAsState()
    val images by viewModel.imageGallery.collectAsState()

    LaunchedEffect(classifiedId) {
        viewModel.getClassified(classifiedId)
        viewModel.getClassifiedImages(classifiedId)
    }

    ClassifiedPreviewContent(
        classified = classified,
        images = images,
        modifier = modifier
    )
}

@Composable
fun ClassifiedPreviewContent(
    classified: ClassifiedResponse?,
    images: List<ImageGallery>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ad Preview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkGreen
        )

        if (classified == null) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            // Horizontal Image Gallery
            if (images.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp).fillMaxWidth()
                ) {
                    items(images) { image ->
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            AsyncImage(
                                model = image.url,
                                contentDescription = image.description,
                                modifier = Modifier.width(280.dp).fillMaxHeight(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = classified.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$${classified.price} ${classified.perUnitType}",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGreen,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = classified.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Location & Contact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${classified.city}, ${classified.state} ${classified.postalCode}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Email: ${classified.emailAddress}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (classified.phoneNumber.isNotBlank()) {
                        Text(
                            text = "Phone: ${classified.phoneNumber}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
fun ClassifiedPreviewPreview() {
    val sampleClassified = ClassifiedResponse(
        classifiedId = Uuid.random(),
        createDate = "",
        lastUpdateDate = "",
        activeDate = "",
        lastActiveDate = "",
        renewalCount = 0,
        categoryId = Uuid.random(),
        adTypeId = Uuid.random(),
        price = 1500.0,
        perUnitType = "per Unit",
        title = "Premium Quality Alfalfa Hay",
        description = "High protein alfalfa hay, harvested in peak condition. Perfect for horses and dairy cattle. 3x3x8 bales, approximately 800 lbs each. Located in prime hay country.",
        city = "Bozeman",
        state = "MT",
        postalCode = "59715",
        emailAddress = "contact@hayfarms.com",
        phoneNumber = "406-555-0123",
        longitude = 0.0,
        latitude = 0.0
    )

    val sampleImages = listOf(
        ImageGallery("bales1.jpg", "Side view of stacked bales", "https://example.com/image1.jpg"),
        ImageGallery("bales2.jpg", "Close up of hay quality", "https://example.com/image2.jpg")
    )

    MaterialTheme {
        Surface {
            ClassifiedPreviewContent(
                classified = sampleClassified,
                images = sampleImages
            )
        }
    }
}
