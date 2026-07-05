package com.green.yp.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.DarkGreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChipItem @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val text: String
)

@OptIn(ExperimentalUuidApi::class, ExperimentalLayoutApi::class)
@Composable
fun ChipSelector(
    items: List<ChipItem>,
    selectedId: Uuid?,
    onItemSelected: (ChipItem) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.Black
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 4
        ) {
            items.forEach { item ->
                val isSelected = item.id == selectedId
                
                FilterChip(
                    selected = isSelected,
                    onClick = { onItemSelected(item) },
                    label = {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = DarkGold,
                        labelColor = DarkGreen,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = DarkGreen,
                        selectedBorderColor = DarkGold,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.dp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
fun ChipSelectorPreview() {
    val items = listOf(
        ChipItem(id = Uuid.random(), text = "Category A"),
        ChipItem(id = Uuid.random(), text = "Category B"),
        ChipItem(id = Uuid.random(), text = "Category C"),
        ChipItem(id = Uuid.random(), text = "Category D"),
        ChipItem(id = Uuid.random(), text = "Category E")
    )
    val selectedId = items[1].id

    MaterialTheme {
        Surface(color = Color.White) {
            ChipSelector(
                title = "Select Category",
                items = items,
                selectedId = selectedId,
                onItemSelected = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
