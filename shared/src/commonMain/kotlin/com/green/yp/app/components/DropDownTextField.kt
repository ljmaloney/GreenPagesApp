package com.green.yp.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> DropdownTextField(
    value: T?,
    onValueChange: (T) -> Unit,
    label: String,
    entries: Array<T>,
    modifier: Modifier = Modifier,
    displayName: (T) -> String = { it.name }
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember(value) {
        mutableStateOf(value?.let(displayName) ?: "")
    }

    val filtered = entries.filter {
        displayName(it).startsWith(text, ignoreCase = true)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
            value = text,
            onValueChange = { newText ->
                text = newText
                expanded = true

                filtered.firstOrNull()?.let(onValueChange)
            },
            label = if (label.isNotEmpty()) { { Text(label) } } else null,
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filtered.forEach { item ->
                DropdownMenuItem(
                    text = { Text(displayName(item)) },
                    onClick = {
                        text = displayName(item)
                        onValueChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}