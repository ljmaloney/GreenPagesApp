package com.green.yp.app.components.classified

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen

@Composable
fun ContactInformation(
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = modifier
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

        // First Name - Required, Alphabetical, Starts with Uppercase
        OutlinedTextField(
            value = firstName,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isLetter() }) {
                    firstName = input
                }
            },
            label = { Text("First Name*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            supportingText = {
                if (firstName.isNotEmpty() && !firstName[0].isUpperCase()) {
                    Text("Must start with an uppercase letter", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // Last Name - Required, Alphabetical, Starts with Uppercase, Hyphen or Space
        OutlinedTextField(
            value = lastName,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isLetter() || it == '-' || it == ' ' }) {
                    lastName = input
                }
            },
            label = { Text("Last Name*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            supportingText = {
                if (lastName.isNotEmpty() && !lastName[0].isUpperCase()) {
                    Text("Must start with an uppercase letter", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // Email Address - Required, Valid format
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            supportingText = {
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
                if (email.isNotEmpty() && !email.matches(emailRegex)) {
                    Text("Invalid email format", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // Phone Number - Required, Valid US format
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { input ->
                if (input.all { it.isDigit() || it == '-' || it == '(' || it == ')' || it == ' ' }) {
                    phoneNumber = input
                }
            },
            label = { Text("Phone Number*") },
            placeholder = { Text("(555) 555-5555") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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

@Preview
@Composable
fun ContactInformationPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ContactInformation()
        }
    }
}
