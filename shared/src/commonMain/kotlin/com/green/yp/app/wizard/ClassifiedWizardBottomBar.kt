package com.green.yp.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.DarkGold

@Composable
fun ClassifiedWizardBottomBar(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPreview: () -> Unit,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button - Hidden on first step
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DarkGreen)
                    )
                ) {
                    Text("<< Back")
                }
            }

            // Next or Preview Button
            if (currentStep < totalSteps - 1) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Next >>")
                }
            } else {
                Button(
                    onClick = onPreview,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGold)
                ) {
                    Text("Preview Ad")
                }
            }
        }
    }
}
