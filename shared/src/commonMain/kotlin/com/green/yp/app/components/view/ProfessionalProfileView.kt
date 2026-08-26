package com.green.yp.app.components.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.green.yp.app.components.ModalSurface
import com.green.yp.app.components.generated.components.AlertDialog as AppAlertDialog
import com.green.yp.app.components.generated.components.card.CardDefaults
import com.green.yp.app.components.generated.components.card.OutlinedCard
import com.green.yp.app.messaging.MessageComponent
import com.green.yp.app.messaging.MessageDraft
import com.green.yp.app.messaging.MessagingViewModel
import com.green.yp.app.shared.dto.search.SearchRecordType
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.DarkGreen
import greenpagesapp.shared.generated.resources.Res
import greenpagesapp.shared.generated.resources.professional_icon_gold
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

fun ProfessionalProfileView(): MarketPlaceViewRenderer = object : MarketPlaceViewRenderer {
    @Composable
    override fun renderView(result: SearchResponseDTO, modifier: Modifier?, onClick: () -> Unit) {
        val isPreview = LocalInspectionMode.current
        val messagingViewModel: MessagingViewModel? = if (isPreview) null else koinViewModel()
        var previewDraft by remember(result.title) {
            mutableStateOf(
                MessageDraft(
                    emailAddress = "",
                    name = "",
                    phoneNumber = "",
                    subject = result.title,
                    message = ""
                )
            )
        }
        val draft = messagingViewModel?.state?.collectAsState()?.value ?: previewDraft
        var showMessageModal by remember { mutableStateOf(false) }
        var sendErrorMessage by remember { mutableStateOf<String?>(null) }
        val uriHandler = LocalUriHandler.current

        Box(
            modifier = (modifier ?: Modifier)
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
                                    text = result.businessName.orEmpty(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DarkGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                if (result.title.isNotBlank() && result.title != result.businessName) {
                                    Text(
                                        text = result.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
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
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
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

                            Column {
                                Text(
                                    text = listOfNotNull(
                                        result.addressLine1?.takeIf { it.isNotBlank() },
                                        result.addressLine2?.takeIf { it.isNotBlank() },
                                        "${result.city}, ${result.state} ${result.postalCode}"
                                    ).joinToString("\n"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray
                                )

                                if (!result.phoneNumber.isNullOrBlank()) {
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

                        if (!result.businessUrl.isNullOrBlank()) {
                            TextButton(
                                onClick = {
                                    try {
                                        uriHandler.openUri(result.businessUrl)
                                    } catch (_: Exception) {
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .offset(y = (-6).dp)
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

            Image(
                painter = painterResource(Res.drawable.professional_icon_gold),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-6).dp)
                    .offset(y = (-10).dp)
                    .size(28.dp)
            )

            if (!result.emailAddress.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Message,
                    contentDescription = null,
                    tint = DarkGreen,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
                        .clickable { showMessageModal = true }
                        .size(24.dp)
                )
            }

            ModalSurface(
                visible = showMessageModal,
                onDismissRequest = { showMessageModal = false },
                header = "Sending Message for ${result.title}"
            ) {
                MessageComponent(
                    draft = draft,
                    isScrollable = false,
                    onDraftChange = { updatedDraft ->
                        messagingViewModel?.updateDraft { updatedDraft } ?: run {
                            previewDraft = updatedDraft
                        }
                    },
                    subject = result.title,
                    onSendMessage = {
                        messagingViewModel?.sendContactMessage(result) { sendResult ->
                            sendResult.onSuccess {
                                showMessageModal = false
                            }
                            sendResult.onFailure { throwable ->
                                sendErrorMessage = messagingViewModel.getSendMessageError(throwable)
                            }
                        } ?: run {
                            showMessageModal = false
                        }
                    },
                    onCancel = { showMessageModal = false }
                )
            }

            sendErrorMessage?.let { errorText ->
                AppAlertDialog(
                    onDismissRequest = { sendErrorMessage = null },
                    onConfirmClick = { sendErrorMessage = null },
                    title = "Message Failed",
                    text = errorText,
                    confirmButtonText = "OK",
                    dismissButtonText = null,
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    textContentColor = Color.DarkGray,
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfessionalProfileViewPreview() {
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
        imageUrl = "https://via.placeholder.com/600x360.png",
        distance = 2.5,
        phoneNumber = "(503) 555-0123",
        description = "We provide eco-friendly landscaping and garden design services focused on native plants and water conservation.",
        longitude = 0.0,
        latitude = 0.0
    )

    MaterialTheme {
        ProfessionalProfileView().renderView(
            result = sampleResult,
            modifier = Modifier,
            onClick = {}
        )
    }
}