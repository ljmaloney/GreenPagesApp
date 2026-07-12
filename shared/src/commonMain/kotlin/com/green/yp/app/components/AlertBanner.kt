package com.green.yp.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.DarkGreen

/**
 * Alert banner component styled to match existing app components (DarkGreen/DarkGold accents, white background, outlined border).
 * Supports multiple stacked, dismissible alerts.
 */

enum class AlertType { INFO, WARNING, ERROR, SUCCESS }

data class AlertBannerItem(
    val id: String,
    val title: String,
    val message: String,
    val type: AlertType = AlertType.INFO
)

@Composable
fun AlertBanner(
    alerts: List<AlertBannerItem>,
    onDismiss: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        alerts.forEach { alert ->
            AlertBannerCard(alert = alert, onDismiss = onDismiss)
        }
    }
}

@Composable
private fun AlertBannerCard(alert: AlertBannerItem, onDismiss: (String) -> Unit) {
    // Match app colors: INFO uses DarkGreen, WARNING/SUCCESS use DarkGold, ERROR uses Material error
    val (titleColor, icon, borderColor) = when (alert.type) {
        AlertType.INFO -> Triple(MaterialTheme.colorScheme.primary, Icons.Default.Info, MaterialTheme.colorScheme.primary)
        AlertType.WARNING -> Triple(DarkGold, Icons.Default.Warning, DarkGold)
        AlertType.ERROR -> Triple(MaterialTheme.colorScheme.error, Icons.Default.Error, MaterialTheme.colorScheme.error)
        AlertType.SUCCESS -> Triple(DarkGreen, Icons.Default.CheckCircle, DarkGreen)
    }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(enabled = true).copy(brush = SolidColor(borderColor)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = titleColor,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)) {
                Text(text = alert.title, style = MaterialTheme.typography.titleSmall, color = titleColor)
                Text(text = alert.message, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }

            IconButton(onClick = { onDismiss(alert.id) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = titleColor)
            }
        }
    }
}

// Helper to create the no-results alert
fun NoSearchResultsAlert(id: String = "no-results") = AlertBannerItem(
    id = id,
    title = "No results found",
    message = "Try widening your search, or check your filters.",
    type = AlertType.WARNING
)

@Preview
@Composable
fun AlertBannerPreview() {
    val items = remember {
        mutableStateListOf(
            NoSearchResultsAlert(),
            AlertBannerItem("warn-1", "Network issue", "Results may be incomplete.", AlertType.WARNING),
            AlertBannerItem("err-1", "Service error", "Unable to contact search service.", AlertType.ERROR)
        )
    }

    AlertBanner(alerts = items, onDismiss = { id -> items.removeAll { it.id == id } })
}
