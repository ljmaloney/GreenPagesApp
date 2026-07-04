package com.green.yp.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.LightGreenGrey

@Composable
fun EmailValidationComponent(
    onValidate: (code: String) -> Unit,
    modifier: Modifier = Modifier,
    headerText: String = "Validate Email Address"
) {
    var code by remember { mutableStateOf(CharArray(8)) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = headerText,
            style = MaterialTheme.typography.titleLarge,
            color = DarkGreen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Helper text
        Text(
            text = "To confirm your email address is working, please check your email and enter the validation token below. Make sure to add greenyp.com to your list of approved senders.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // OTP Input Fields
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(8) { index ->
                OTPInputField(
                    value = code.getOrNull(index)?.toString() ?: "",
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isLetterOrDigit() }
                        if (filtered.length <= 1) {
                            code[index] = if (filtered.isEmpty()) '\u0000' else filtered[0]
                        }
                    },
                    modifier = Modifier
                        .width(40.dp)
                        .height(48.dp)
                )
            }
        }

        // Validate Button
        Button(
            onClick = { 
                val validationCode = code
                    .filter { it != '\u0000' }
                    .joinToString("")
                onValidate(validationCode)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen
            ),
            enabled = code.count { it != '\u0000' } == 8
        ) {
            Text("Validate Email")
        }
    }
}

@Composable
private fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .background(Color.White, shape = MaterialTheme.shapes.small)
            .border(1.dp, DarkGreen, shape = MaterialTheme.shapes.small),
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
