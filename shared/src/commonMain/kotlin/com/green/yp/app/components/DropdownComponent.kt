package com.green.yp.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DropdownItem(
    val uuid: Uuid,
    val stringId: String,
    val description: String,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun DropdownComponent(
    options: List<DropdownItem>,
    selectedOption: DropdownItem?,
    onOptionSelected: (DropdownItem) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Label"
) {
    var expanded by remember { mutableStateOf(value = false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
            readOnly = true,
            value = selectedOption?.description ?: "",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.description) },
                    onClick = {
                        onOptionSelected(item)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
fun DropdownComponentPreview() {
    val options = listOf(
        DropdownItem(Uuid.random(), "1", "Option 1"),
        DropdownItem(Uuid.random(), "2", "Option 2"),
        DropdownItem(Uuid.random(), "3", "Option 3")
    )
    var selectedOption by remember { mutableStateOf<DropdownItem?>(options[0]) }

    MaterialTheme {
        DropdownComponent(
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it }
        )
    }
}
