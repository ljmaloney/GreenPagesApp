package com.green.yp.app.wizard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.components.DropdownTextField
import com.green.yp.app.enum.StateEnum
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.wizard.ClassifiedWizardViewModel

@Composable
fun AdLocation(
    modifier: Modifier = Modifier,
    wizardViewModel: ClassifiedWizardViewModel? = null
) {
    val address = wizardViewModel?.state?.collectAsState()?.value?.draft?.address ?: ""
    val city = wizardViewModel?.state?.collectAsState()?.value?.draft?.city ?: ""
    val state = wizardViewModel?.state?.collectAsState()?.value?.draft?.state ?: ""
    val zipCode = wizardViewModel?.state?.collectAsState()?.value?.draft?.postalCode ?: ""
    val selectedState = if (state.isNotEmpty()) StateEnum.valueOf(state) else null

    Column(
        modifier = modifier
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
        OutlinedTextField(
            value = address,
            onValueChange = { input ->
                if (input.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                    wizardViewModel?.updateAddress(input)
                }
            },
            label = { Text("Address*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        // City Field - Required
        OutlinedTextField(
            value = city,
            onValueChange = { 
                wizardViewModel?.updateCity(it)
            },
            label = { Text("City*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        // State Field - DropDownTextField with StateEnum
        DropdownTextField(
            value = selectedState,
            onValueChange = { 
                wizardViewModel?.updateStateCode(it.name)
            },
            label = "State*",
            entries = StateEnum.entries.toTypedArray(),
            displayName = { it.displayName }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Zip Code Field - Numbers and Hyphen
        OutlinedTextField(
            value = zipCode,
            onValueChange = { input ->
                if (input.all { it.isDigit() || it == '-' }) {
                    wizardViewModel?.updatePostalCode(input)
                }
            },
            label = { Text("Zip Code*") },
            placeholder = { Text("12345 or 12345-6789") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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

@Preview
@Composable
fun AdLocationPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AdLocation()
        }
    }
}
