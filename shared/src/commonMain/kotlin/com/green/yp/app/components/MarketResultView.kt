package com.green.yp.app.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.dto.search.SearchRecordType
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.DarkGold

/**
 * A search result component using a Lumo-style card as the base.
 * Displays key information from a [SearchResponseDTO].
 */
@Composable
fun MarketResultView(
    result: SearchResponseDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp) // Increased vertical padding to accommodate overlapping badge
    ) {
        // We use OutlinedCard as the base for the "Lumo" look, 
        // styled with the app's DarkGreen and DarkGold theme.
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.White,
            ),
            border = CardDefaults.outlinedCardBorder(enabled = true).copy(
                brush = androidx.compose.ui.graphics.SolidColor(DarkGreen)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Business Icon
                        if (!result.businessIconUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = result.businessIconUrl,
                                contentDescription = "${result.businessName} logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Column {
                            Text(
                                text = result.businessName,
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkGreen,
                                fontWeight = FontWeight.Bold
                            )
                            if (!result.title.isNullOrBlank() && !result.title.equals(result.businessName)) {
                                Text(
                                    text = result.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    // Distance badge
                    Surface(
                        color = DarkGold.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "${result.distance.toInt()} mi",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = DarkGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val isFullAddressShown = result.recordType in listOf(
                    SearchRecordType.GREEN_PRO,
                    SearchRecordType.GREEN_PRO_SERVICE,
                    SearchRecordType.GREEN_PRO_PRODUCT
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top // Align to top so icon/website stay with first line
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Top // Align icon with top line of text
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp) // Slight offset to center with first line of text
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Column {
                            Text(
                                text = if (isFullAddressShown) {
                                    listOfNotNull(
                                        result.addressLine1,
                                        result.addressLine2.takeIf { !it.isNullOrBlank() },
                                        "${result.city}, ${result.state} ${result.postalCode}"
                                    ).joinToString("\n")
                                } else {
                                    "${result.city}, ${result.state}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )

                            if (isFullAddressShown && !result.phoneNumber.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = DarkGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = result.phoneNumber,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    // Website Link - Now stays at the top level
                    if (!result.businessUrl.isNullOrBlank()) {
                        TextButton(
                            onClick = { 
                                try {
                                    uriHandler.openUri(result.businessUrl)
                                } catch (_: Exception) {
                                    // Handle invalid URI
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .offset(y = (-6).dp) // Nudge up to better align with first line of text
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = DarkGold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Website",
                                style = MaterialTheme.typography.labelMedium,
                                color = DarkGold,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }

                if (!result.description.isNullOrBlank()) {
                    var isExpanded by remember { mutableStateOf(false) }
                    var isTextTruncated by remember { mutableStateOf(false) }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(modifier = Modifier.animateContentSize()) {
                        Text(
                            text = result.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Black,
                            onTextLayout = { textLayoutResult ->
                                if (!isExpanded) {
                                    isTextTruncated = textLayoutResult.hasVisualOverflow
                                }
                            }
                        )
                        if (isTextTruncated && !isExpanded) {
                            Text(
                                text = "(more)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DarkGold,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .clickable { isExpanded = true }
                            )
                        }
                    }
                }
            }
        }

        // Category Badge - Positioned to overlap the top border
        result.categoryName?.let { category ->
            Surface(
                color = DarkGreen,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 24.dp) // Move over to the left
                    .offset(y = (-10).dp) // Move up so border intersects the middle
            ) {
                Text(
                    text = category.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun MarketResultViewPreview() {
    val sampleResult = SearchResponseDTO(
        externId = "1",
        producerId = "p1",
        locationId = "l1",
        categoryRef = "cat1",
        categoryName = "Gardening",
        recordType = SearchRecordType.GREEN_PRO,
        active = true,
        title = "Sustainable Landscaping Services",
        businessName = "Green Thumb Solutions",
        businessUrl = "https://example.com",
        businessIconUrl = "https://via.placeholder.com/150",
        city = "Portland",
        state = "OR",
        postalCode = "97201",
        addressLine1 = "123 Eco Way",
        addressLine2 = "Suite 400",
        distance = 2.5,
        phoneNumber = "(503) 555-0123",
        description = "We provide eco-friendly landscaping and garden design services focused on native plants and water conservation.",
        longitude = 0.0,
        latitude = 0.0
    )

    MaterialTheme {
        Column {
            Text("GREEN_PRO", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            MarketResultView(result = sampleResult)

            Text("CLASSIFIED", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            MarketResultView(result = sampleResult.copy(
                recordType = SearchRecordType.CLASSIFIED,
                businessName = "Used Garden Tools",
                title = "Gently used lawnmower"
            ))

            Text("GREEN_PRO_SERVICE", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            MarketResultView(result = sampleResult.copy(
                recordType = SearchRecordType.GREEN_PRO_SERVICE,
                businessName = "EcoClean Windows",
                title = "Solar Panel Cleaning"
            ))

            Text("GREEN_PRO_PRODUCT", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            MarketResultView(result = sampleResult.copy(
                recordType = SearchRecordType.GREEN_PRO_PRODUCT,
                businessName = "Organic Seeds Co.",
                title = "Heirloom Tomato Seeds"
            ))
        }
    }
}
