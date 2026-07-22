package com.green.yp.app.components.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.green.yp.app.PreviewContext
import com.green.yp.app.shared.dto.search.SearchRecordType
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.DarkGreen
import greenpagesapp.shared.generated.resources.Res
import greenpagesapp.shared.generated.resources.classifieds_icon_gold
import greenpagesapp.shared.generated.resources.classifieds_icon_hanging
import org.jetbrains.compose.resources.painterResource

fun ClassifiedView(): MarketPlaceViewRenderer = object : MarketPlaceViewRenderer {
    @Composable
    override fun renderView(result: SearchResponseDTO, modifier: Modifier?, onClick: () -> Unit) {
        Box(
            modifier = (modifier ?: Modifier)
                .background(Color.White)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
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
                            Column {
                                Text(
                                    text = result.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DarkGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

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

                    if (!result.imageUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = result.imageUrl,
                            contentDescription = result.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = DarkGreen,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "${result.city}, ${result.state}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
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

            Image(
                painter = painterResource(Res.drawable.classifieds_icon_gold),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-2).dp)
                    .offset(y = (-12).dp)
                    .size(28.dp)
            )

            result.categoryName?.let { category ->
                Surface(
                    color = DarkGreen,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 24.dp)
                        .offset(y = (-10).dp)
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

}

@Preview
@Composable
fun ClassifiedViewPreview() {
    PreviewContext()
    val sampleClassified = SearchResponseDTO(
        externId = "1",
        producerId = "p1",
        locationId = "l1",
        categoryRef = "cat1",
        categoryName = "Gardening",
        recordType = SearchRecordType.CLASSIFIED,
        active = true,
        title = "Gently used lawnmower",
        businessName = "Used Garden Tools",
        businessUrl = "https://example.com",
        businessIconUrl = null,
        city = "Portland",
        state = "OR",
        postalCode = "97201",
        addressLine1 = "123 Eco Way",
        addressLine2 = "Suite 400",
        imageUrl = "https://via.placeholder.com/600x360.png",
        distance = 2.5,
        phoneNumber = "(503) 555-0123",
        description = "Great condition electric lawnmower with recently replaced battery. Pickup only.",
        longitude = 0.0,
        latitude = 0.0
    )

    MaterialTheme {
        ClassifiedView().renderView(
            result = sampleClassified,
            modifier = Modifier,
            onClick = {}
        )
    }
}
