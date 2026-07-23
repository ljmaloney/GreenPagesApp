package com.green.yp.app.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.dto.message.MessageDTO
import com.green.yp.app.ui.theme.DarkGreen

@Composable
fun MessageComponent(
    subject: String,
    onSendMessage: (MessageDTO) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    companyName: String? = null
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var editableSubject by remember(subject) { mutableStateOf(subject) }
    var message by remember { mutableStateOf("") }
    var showValidation by remember { mutableStateOf(false) }

    val isNameValid = name.isNotBlank()
    val isEmailValid = email.isNotBlank()
    val isSubjectValid = editableSubject.isNotBlank()
    val isMessageValid = message.isNotBlank()
    val isFormValid = isNameValid && isEmailValid && isSubjectValid && isMessageValid
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = DarkGreen,
        unfocusedBorderColor = DarkGreen,
        focusedLabelColor = DarkGreen,
        cursorColor = DarkGreen
    )

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name *") },
            isError = showValidation && !isNameValid,
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Your Email *") },
            isError = showValidation && !isEmailValid,
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = formatUsPhone(it) },
            label = { Text("Your Phone") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = editableSubject,
            onValueChange = { editableSubject = it },
            label = { Text("Subject *") },
            isError = showValidation && !isSubjectValid,
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message *") },
            isError = showValidation && !isMessageValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            colors = textFieldColors,
            minLines = 4
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    showValidation = true
                    if (isFormValid) {
                        onSendMessage(
                            MessageDTO(
                                companyName = companyName,
                                emailAddress = email.trim(),
                                name = name.trim(),
                                phoneNumber = phone,
                                subject = editableSubject.trim(),
                                message = message.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(" Send Message")
            }

            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
        }
    }
}

private fun formatUsPhone(input: String): String {
    val digits = input.filter { it.isDigit() }.take(10)
    if (digits.isEmpty()) return ""

    return buildString {
        append("(")
        append(digits.take(3))
        if (digits.length >= 4) {
            append(") ")
            append(digits.substring(3, minOf(6, digits.length)))
        }
        if (digits.length >= 7) {
            append("-")
            append(digits.substring(6, digits.length))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageComponentPreview() {
    MaterialTheme {
        MessageComponent(
            subject = "Question about listing",
            onSendMessage = {},
            onCancel = {}
        )
    }
}