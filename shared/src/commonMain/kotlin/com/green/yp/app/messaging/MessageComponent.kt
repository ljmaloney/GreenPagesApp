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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.Gold100

@Composable
fun MessageComponent(
    draft: MessageDraft,
    onDraftChange: (MessageDraft) -> Unit,
    subject: String,
    onSendMessage: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isScrollable: Boolean = true
) {
    val editableSubject = if (draft.subject.isBlank()) subject else draft.subject
    var showValidation by remember { mutableStateOf(false) }
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val phoneRegex = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$".toRegex()

    val isNameValid = isNameValid(draft.name)
    val isEmailValid = draft.emailAddress.isNotBlank() && draft.emailAddress.matches(emailRegex)
    val isSubjectValid = editableSubject.isNotBlank()
    val isMessageValid = draft.message.isNotBlank()
    val isFormValid = isNameValid && isEmailValid && isSubjectValid && isMessageValid
    var nameInput by remember(draft.name) { mutableStateOf(draft.name) }
    var phoneInput by remember(draft.phoneNumber) { mutableStateOf(draft.phoneNumber) }
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
            .then(if (isScrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = nameInput,
            onValueChange = {
                nameInput = it
                onDraftChange(draft.copy(name = it))
            },
            label = { Text("Your Name *") },
            isError = showValidation && !isNameValid,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        val formatted = formatNameInput(nameInput)
                        nameInput = formatted
                        onDraftChange(draft.copy(name = formatted))
                    }
                },
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )

        OutlinedTextField(
            value = draft.emailAddress,
            onValueChange = { onDraftChange(draft.copy(emailAddress = it)) },
            label = { Text("Your Email *") },
            placeholder = { Text("you@example.com") },
            isError = showValidation && !isEmailValid,
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            supportingText = {
                if (draft.emailAddress.isNotEmpty() && !draft.emailAddress.matches(emailRegex)) {
                    Text("Invalid email format", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        OutlinedTextField(
            value = phoneInput,
            onValueChange = { input ->
                if (input.all { it.isDigit() || it == '-' || it == '(' || it == ')' || it == ' ' || it == '+' }) {
                    phoneInput = input
                    onDraftChange(draft.copy(phoneNumber = input))
                }
            },
            label = { Text("Your Phone") },
            placeholder = { Text("(555) 555-5555") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        val digitsOnly = phoneInput.filter { it.isDigit() }
                        if (digitsOnly.length == 10) {
                            val formatted = "(${digitsOnly.substring(0, 3)}) ${digitsOnly.substring(3, 6)}-${digitsOnly.substring(6)}"
                            phoneInput = formatted
                            onDraftChange(draft.copy(phoneNumber = formatted))
                        }
                    }
                },
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            supportingText = {
                if (phoneInput.isNotEmpty() && !phoneInput.matches(phoneRegex)) {
                    Text("Invalid US phone number format", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        OutlinedTextField(
            value = editableSubject,
            onValueChange = { onDraftChange(draft.copy(subject = it)) },
            label = { Text("Subject *") },
            isError = showValidation && !isSubjectValid,
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = draft.message,
            onValueChange = { onDraftChange(draft.copy(message = it)) },
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
                        onSendMessage()
                    }
                },
                enabled = isFormValid,
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
                    containerColor = Gold100,
                    contentColor = DarkGreen
                )
            ) {
                Text("Cancel")
            }
        }
    }
}

private fun isNameValid(input: String): Boolean {
    if (input.isBlank()) return false
    if (input.first().isWhitespace() || input.last().isWhitespace()) return false
    if (input.contains("  ")) return false
    if (input.first().isLowerCase()) return false

    return input.all {
        it.isLetter() || it == ' ' || it == '-' || it == '.' || it == '\''
    }
}

private fun formatNameInput(input: String): String {
    val builder = StringBuilder(input.length)
    var capitalizeNext = true

    input.forEach { char ->
        when {
            char.isLetter() -> {
                builder.append(if (capitalizeNext) char.uppercaseChar() else char.lowercaseChar())
                capitalizeNext = false
            }

            else -> {
                builder.append(char)
                if (char == ' ') {
                    capitalizeNext = true
                }
            }
        }
    }

    return builder.toString()
}

@Preview(showBackground = true)
@Composable
private fun MessageComponentPreview() {
    MaterialTheme {
        MessageComponent(
            draft = MessageDraft(
                emailAddress = "",
                name = "",
                phoneNumber = "",
                subject = "",
                message = ""
            ),
            onDraftChange = {},
            subject = "Question about listing",
            onSendMessage = {},
            onCancel = {}
        )
    }
}