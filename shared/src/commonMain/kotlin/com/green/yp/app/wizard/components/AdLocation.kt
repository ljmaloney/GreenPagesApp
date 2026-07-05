package com.green.yp.app.wizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.components.DropdownTextField
import com.green.yp.app.enum.StateEnum
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.wizard.ClassifiedDraft

@Composable
fun AdLocation(
    draft: ClassifiedDraft,
    onDraftChange: (ClassifiedDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    val address = draft.address
    val city = draft.city
    val state = draft.state
    val zipCode = draft.postalCode
    val selectedState = if (state.isNotEmpty()) StateEnum.valueOf(state) else null

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "3. Enter Ad Location",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = buildAnnotatedString {
                append("The address you provide below helps determine your location so that listings can be sorted by distance from potential buyers. ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Only the city, state, and ZIP code will be visible in your ad")
                }
                append(" — your street address will remain private.")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Address Field - Required, Alphanumeric
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Address*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = address,
                onValueChange = { input ->
                    if (input.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                        onDraftChange(draft.copy(address = input))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // City Field - Required
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "City*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = city,
                onValueChange = { 
                    onDraftChange(draft.copy(city = it))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // State Field - DropDownTextField with StateEnum
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "State*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DropdownTextField(
                value = selectedState,
                onValueChange = { 
                    onDraftChange(draft.copy(state = it.name))
                },
                label = "",
                entries = StateEnum.entries.toTypedArray(),
                modifier = Modifier.fillMaxWidth(),
                displayName = { it.displayName }
            )
        }

        // Zip Code Field - Numbers and Hyphen
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Zip Code*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = zipCode,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '-' }) {
                        onDraftChange(draft.copy(postalCode = input))
                    }
                },
                placeholder = { Text("12345 or 12345-6789") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    val isValid = zipCode.matches(Regex("""^(\d{5}(-\d{4})?|\d{9})$"""))
                    if (zipCode.isNotEmpty() && !isValid) {
                        Text("Invalid format. Use 5 digits, 9 digits, or 5-4 format.", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun AdLocationPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AdLocation(draft = ClassifiedDraft(), onDraftChange = {})
        }
    }
}
