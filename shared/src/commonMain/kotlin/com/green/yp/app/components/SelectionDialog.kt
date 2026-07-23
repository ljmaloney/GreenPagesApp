package com.green.yp.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> SelectionField(
    label: String,
    value: T?,
    items: List<T>,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "Select...",
    onSelected: (T) -> Unit
){}

@Composable
fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    selected: T?,
    label: (T) -> String,
    onSelected: (T) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column {
                Text(title)

                LazyColumn(
                    modifier = Modifier.height(400.dp)
                ) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelected(item)
                                }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = label(item),
                                modifier = Modifier.weight(1f)
                            )

                            if (item == selected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
