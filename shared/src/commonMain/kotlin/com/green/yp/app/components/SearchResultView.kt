package com.green.yp.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.dto.search.SearchRecordType
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.DarkGold

/**
 * A search result component using a Lumo-style card as the base.
 * Displays key information from a [SearchResponseDTO].
 */
@Composable
fun SearchResultView(
    result: SearchResponseDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = result.businessName,
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = result.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DarkGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${result.city}, ${result.state}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }

                if (!result.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        color = Color.Black
                    )
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
fun SearchResultViewPreview() {
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
        city = "Portland",
        state = "OR",
        postalCode = "97201",
        addressLine1 = "123 Eco Way",
        distance = 2.5,
        description = "We provide eco-friendly landscaping and garden design services focused on native plants and water conservation.",
        longitude = 0.0,
        latitude = 0.0
    )

    MaterialTheme {
        Column {
            Text("GREEN_PRO", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            SearchResultView(result = sampleResult)

            Text("CLASSIFIED", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            SearchResultView(result = sampleResult.copy(
                recordType = SearchRecordType.CLASSIFIED,
                businessName = "Used Garden Tools",
                title = "Gently used lawnmower"
            ))

            Text("GREEN_PRO_SERVICE", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            SearchResultView(result = sampleResult.copy(
                recordType = SearchRecordType.GREEN_PRO_SERVICE,
                businessName = "EcoClean Windows",
                title = "Solar Panel Cleaning"
            ))

            Text("GREEN_PRO_PRODUCT", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            SearchResultView(result = sampleResult.copy(
                recordType = SearchRecordType.GREEN_PRO_PRODUCT,
                businessName = "Organic Seeds Co.",
                title = "Heirloom Tomato Seeds"
            ))
        }
    }
}
