package com.green.yp.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.components.generated.components.otptextfield.OTPTextField
import com.green.yp.app.components.generated.components.otptextfield.OTPTextFieldDefaults
import com.green.yp.app.components.generated.components.otptextfield.rememberOtpState
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
    var otpResetCounter by remember { mutableStateOf(0) }
    val otpState = key(otpResetCounter) { rememberOtpState(8) }
    val code = otpState.code.trim()
    val otpColors = OTPTextFieldDefaults.filledColors().copy(
        focusedOutlineColor = DarkGreen,
        unfocusedOutlineColor = DarkGreen,
        disabledOutlineColor = DarkGreen,
        errorOutlineColor = DarkGreen
    )
    var showSuccessMessage by remember(isValidated) { mutableStateOf(isValidated) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxWidth()
            .verticalScroll(scrollState)
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

        OTPTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            state = otpState,
            colors = otpColors,
            isError = error != null,
            onComplete = {}
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    otpResetCounter++
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
        
        Spacer(modifier = Modifier.height(24.dp))
    }
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
