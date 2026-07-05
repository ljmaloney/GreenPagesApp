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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.wizard.ClassifiedDraft

@Composable
fun ContactInformation(
    draft: ClassifiedDraft,
    onDraftChange: (ClassifiedDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstName = draft.firstName
    val lastName = draft.lastName
    val email = draft.emailAddress
    val phoneNumber = draft.phoneNumber

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "4. Enter Contact Information",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // First Name - Required
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "First Name*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = firstName,
                onValueChange = { input ->
                    if (input.isEmpty() || input.all { it.isLetter() }) {
                        onDraftChange(draft.copy(firstName = input))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    if (firstName.isNotEmpty() && !firstName[0].isUpperCase()) {
                        Text("Must start with an uppercase letter", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }

        // Last Name - Required
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Last Name*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { input ->
                    if (input.isEmpty() || input.all { it.isLetter() || it == '-' || it == ' ' }) {
                        onDraftChange(draft.copy(lastName = input))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    if (lastName.isNotEmpty() && !lastName[0].isUpperCase()) {
                        Text("Must start with an uppercase letter", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }

        // Email Address - Required
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Email Address*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    onDraftChange(draft.copy(emailAddress = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
                    if (email.isNotEmpty() && !email.matches(emailRegex)) {
                        Text("Invalid email format", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }

        // Phone Number - Required
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Phone Number*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { input ->
                    // Allow digits and common phone formatting characters
                    if (input.all { it.isDigit() || it == '-' || it == '(' || it == ')' || it == ' ' || it == '+' }) {
                        val digitsOnly = input.filter { it.isDigit() }
                        
                        // Automatically format to US standard (XXX) XXX-XXXX when 10 digits are present
                        val formatted = if (digitsOnly.length == 10 && !input.matches(Regex("""^\(\d{3}\) \d{3}-\d{4}$"""))) {
                            "(${digitsOnly.substring(0, 3)}) ${digitsOnly.substring(3, 6)}-${digitsOnly.substring(6)}"
                        } else {
                            input
                        }
                        onDraftChange(draft.copy(phoneNumber = formatted))
                    }
                },
                placeholder = { Text("(555) 555-5555") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    val phoneRegex = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$".toRegex()
                    if (phoneNumber.isNotEmpty() && !phoneNumber.matches(phoneRegex)) {
                        Text("Invalid US phone number format", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun ContactInformationPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ContactInformation(draft = ClassifiedDraft(), onDraftChange = {})
        }
    }
}
