package com.green.yp.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen

@Composable
fun EmailValidationComponent(
    onValidate: (code: String) -> Unit,
    modifier: Modifier = Modifier,
    headerText: String = "Validate Email Address",
    isLoading: Boolean = false,
    error: String? = null,
    isValidated: Boolean = false,
    onClearError: () -> Unit = {},
    onDismissSuccess: () -> Unit = {}
) {
    var code by remember { mutableStateOf("") }
    val focusRequesters = remember { List(8) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    var showSuccessMessage by remember(isValidated) { mutableStateOf(isValidated) }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (error != null) {
            AlertBanner(
                alerts = listOf(
                    AlertBannerItem(
                        id = "email-validation-error",
                        title = "Validation Error",
                        message = error,
                        type = AlertType.ERROR
                    )
                ),
                onDismiss = { onClearError() }
            )
        }

        if (showSuccessMessage) {
            AlertBanner(
                alerts = listOf(
                    AlertBannerItem(
                        id = "email-validation-success",
                        title = "Email Verified",
                        message = "Thanks for taking the time to verify your email address",
                        type = AlertType.INFO
                    )
                ),
                onDismiss = {
                    showSuccessMessage = false
                    onDismissSuccess()
                }
            )
        }

        // Header and Helper text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.titleLarge,
                color = DarkGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "To confirm your email address is working, please check your email and enter the validation token below. Make sure to add greenyp.com to your list of approved senders.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }

        // OTP Input Fields
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(8) { index ->
                val char = code.getOrNull(index)?.toString() ?: ""
                OTPInputField(
                    value = char,
                    onValueChange = { newValue ->
                        if (newValue.length > 1) {
                            // Handle Paste
                            val filtered = newValue.filter { it.isLetterOrDigit() }
                            if (filtered.isNotEmpty()) {
                                var newCode = code.padEnd(8, ' ')
                                for (i in filtered.indices) {
                                    if (index + i < 8) {
                                        newCode = newCode.replaceRange(index + i, index + i + 1, filtered[i].toString())
                                    }
                                }
                                code = newCode.substring(0, 8).trimEnd()
                                
                                val nextFocusIndex = (index + filtered.length).coerceAtMost(7)
                                if (code.length >= 8) focusManager.clearFocus()
                                else focusRequesters[nextFocusIndex].requestFocus()
                            }
                        } else if (newValue.isNotEmpty()) {
                            // Handle Single Character
                            val filtered = newValue.filter { it.isLetterOrDigit() }
                            if (filtered.isNotEmpty()) {
                                val newCode = if (index < code.length) {
                                    code.replaceRange(index, index + 1, filtered.take(1))
                                } else if (index == code.length) {
                                    code + filtered.take(1)
                                } else {
                                    code
                                }
                                
                                if (newCode.length <= 8) {
                                    code = newCode
                                    if (index < 7) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                }
                            }
                        } else if (index < code.length) {
                            // Handle backspace when current field was not empty
                            code = code.removeRange(index, index + 1)
                            if (index > 0) {
                                focusRequesters[index - 1].requestFocus()
                            }
                        }
                    },
                    onBackspace = {
                        // Handle backspace when current field is already empty
                        if (index > 0) {
                            focusRequesters[index - 1].requestFocus()
                        }
                    },
                    modifier = Modifier
                        .requiredWidth(36.dp)
                        .requiredHeight(48.dp)
                        .focusRequester(focusRequesters[index])
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    code = ""
                    focusRequesters[0].requestFocus()
                },
                modifier = Modifier.height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    contentColor = Color.White
                ),
                enabled = code.isNotEmpty() && !isLoading
            ) {
                Text("Clear")
            }

            Button(
                onClick = {
                    onValidate(code)
                },
                modifier = Modifier.height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen
                ),
                enabled = code.length == 8 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Validate Email")
                }
            }
        }
    }
}

@Composable
private fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .background(Color.White, shape = MaterialTheme.shapes.small)
            .border(1.dp, DarkGreen, shape = MaterialTheme.shapes.small)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace && value.isEmpty()) {
                    onBackspace()
                    true
                } else {
                    false
                }
            },
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            textAlign = TextAlign.Center,
            color = DarkGreen
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EmailValidationComponentPreview() {
    MaterialTheme {
        Box(Modifier.background(Color.White)) {
            EmailValidationComponent(
                onValidate = { code ->
                    println("Validation code: $code")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailValidationComponentCustomHeaderPreview() {
    MaterialTheme {
        Box(Modifier.background(Color.White)) {
            EmailValidationComponent(
                onValidate = { code ->
                    println("Validation code: $code")
                },
                headerText = "Verify Your Email"
            )
        }
    }
}
