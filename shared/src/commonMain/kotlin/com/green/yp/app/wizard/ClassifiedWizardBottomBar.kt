package com.green.yp.app.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.LightLightGold

@Composable
fun ClassifiedWizardBottomBar(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPreview: () -> Unit,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    viewModel: ClassifiedWizardViewModel? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LightLightGold,
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
                    enabled = !isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = SolidColor(DarkGreen)
                    )
                ) {
                    Text("<< Back")
                }
            }

            // Next or Preview Button
            if (currentStep < totalSteps - 1) {
                Button(
                    onClick = onNext,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = DarkGreen
                        )
                    } else {
                        Text("Next >>")
                    }
                }
            } else {
                Button(
                    onClick = onPreview,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGold)
                ) {
                    Text("Preview Ad")
                }
            }
        }
    }
}
