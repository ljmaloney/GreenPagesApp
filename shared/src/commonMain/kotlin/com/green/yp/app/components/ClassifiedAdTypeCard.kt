package com.green.yp.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.green.yp.app.shared.dto.classified.ClassifiedAdFeatures
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.DarkGold

/**
 * A card component displaying details for a [ClassifiedAdType].
 * Follows the Lumo-style OutlinedCard design.
 */
@OptIn(ExperimentalUuidApi::class)
@Composable
fun ClassifiedAdTypeCard(
    adType: ClassifiedAdType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.White,
            ),
            border = CardDefaults.outlinedCardBorder(enabled = true).copy(
                brush = SolidColor(if (adType.defaultPackage) DarkGold else DarkGreen)
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = adType.adTypeName,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGreen,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (adType.defaultPackage) {
                        Surface(
                            color = DarkGold,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "POPULAR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${adType.monthlyPrice.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "/mo",
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                if (adType.threeMonthPrice > 0) {
                    Text(
                        text = "or $${adType.threeMonthPrice.toInt()} for 3 months",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkGreen.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Features list
                adType.features.features.forEach { feature ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }

                if (adType.features.maxImages > 0) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Up to ${adType.features.maxImages} images",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }

                if (adType.features.protectContact) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Protected contact info",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@OptIn(ExperimentalUuidApi::class)
fun AdTypeCardPreview() {
    val sampleFeatures = ClassifiedAdFeatures(
        features = listOf(
            "Enhanced Search Visibility",
            "Unlimited Business Description",
            "Priority Support",
            "Social Media Links"
        ),
        maxImages = 10,
        protectContact = true
    )

    val sampleAdType = ClassifiedAdType(
        adTypeId = Uuid.random(),
        createDate = "2024-01-01T00:00:00Z",
        active = true,
        defaultPackage = true,
        adTypeName = "Professional Plus",
        monthlyPrice = 29.99,
        threeMonthPrice = 75.00,
        features = sampleFeatures
    )

    MaterialTheme {
        Surface(color = Color.LightGray.copy(alpha = 0.1f)) {
            ClassifiedAdTypeCard(adType = sampleAdType)
        }
    }
}

