package com.green.yp.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.enum.PricePerEnum

@Composable
fun <T> ThreeItemSpinner(
    items: List<T>,
    selectedIndex: Int,
    itemLabel: (T) -> String,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val previous =
            if (selectedIndex > 0)
                items[selectedIndex - 1]
            else null

        val current =
            items[selectedIndex]

        val next =
            if (selectedIndex < items.lastIndex)
                items[selectedIndex + 1]
            else null

        previous?.let {
            Text(
                text = itemLabel(it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .50f),
                modifier = Modifier
                    .clickable {
                        onSelected(selectedIndex - 1)
                    }
                    .padding(vertical = 8.dp)
            )
        }

        Surface(
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = itemLabel(current),
                modifier = Modifier.padding(
                    horizontal = 24.dp,
                    vertical = 12.dp
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        next?.let {
            Text(
                text = itemLabel(it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .50f),
                modifier = Modifier
                    .clickable {
                        onSelected(selectedIndex + 1)
                    }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun ThreeItemSpinnerPreview() {
    MaterialTheme {
        Surface {
            ThreeItemSpinner(
                items = PricePerEnum.entries,
                selectedIndex = 2,
                itemLabel = { it.displayName },
                onSelected = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
