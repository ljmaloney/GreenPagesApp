package com.green.yp.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> SearchableDropdown(
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