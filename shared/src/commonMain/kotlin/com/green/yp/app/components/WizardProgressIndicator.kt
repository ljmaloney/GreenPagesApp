package com.green.yp.app.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.LightGreen

data class WizardStep(
    val title: String
)

@Composable
fun WizardProgressIndicator(
    steps: List<WizardStep>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxWidth()
    ) {

        when {
            maxWidth >= 840.dp -> {
                FullStepper(
                    steps = steps,
                    currentStep = currentStep
                )
            }

            maxWidth >= 400.dp -> {
                CompactStepper(
                    steps = steps,
                    currentStep = currentStep
                )
            }

            else -> {
                SmallStepper(
                    steps = steps,
                    currentStep = currentStep
                )
            }
        }
    }
}

@Composable
private fun FullStepper(
    steps: List<WizardStep>,
    currentStep: Int
) {
    Box(modifier = Modifier.fillMaxWidth()) {

        // Connecting line behind the circles. Weights position each segment
        // so it spans exactly from one circle's horizontal center to the next
        // (circle i sits at (i + 0.5) / steps.size of the row width).
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            repeat(steps.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(2f)
                        .padding(top = 9.dp),
                    thickness = 2.dp,
                    color = DarkGreen
                )
            }
            Spacer(Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            steps.forEachIndexed { index, step ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StepCircle(
                        state = when {
                            index < currentStep -> StepState.Completed
                            index == currentStep -> StepState.Current
                            else -> StepState.Pending
                        }
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = step.title,
                        fontWeight = if (index == currentStep)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactStepper(
    steps: List<WizardStep>,
    currentStep: Int
) {
    val previous =
        if (currentStep > 0)
            steps[currentStep - 1].title
        else null

    val current =
        steps[currentStep].title

    val next =
        if (currentStep < steps.lastIndex)
            steps[currentStep + 1].title
        else null

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {

            // Connecting line behind the circles. The row is divided into
            // 6 equal weight units: extension slots (weight 1) trail past
            // the outer circles when hidden earlier/later steps exist,
            // and the two inner segments (weight 2) connect the visible
            // circle centers at 1/6, 3/6, 5/6 of the row width.
            val extendLeft = currentStep >= 2
            val extendRight = currentStep <= steps.lastIndex - 2

            val extendLeftColor by animateColorAsState(
                targetValue = if (extendLeft) DarkGreen else Color.Transparent,
                label = "compact-extend-left"
            )
            val prevLineColor by animateColorAsState(
                targetValue = if (previous != null) DarkGreen else Color.Transparent,
                label = "compact-prev-line"
            )
            val nextLineColor by animateColorAsState(
                targetValue = if (next != null) DarkGreen else Color.Transparent,
                label = "compact-next-line"
            )
            val extendRightColor by animateColorAsState(
                targetValue = if (extendRight) DarkGreen else Color.Transparent,
                label = "compact-extend-right"
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 9.dp),
                    thickness = 2.dp,
                    color = extendLeftColor
                )
                HorizontalDivider(
                    modifier = Modifier
                        .weight(2f)
                        .padding(top = 9.dp),
                    thickness = 2.dp,
                    color = prevLineColor
                )
                HorizontalDivider(
                    modifier = Modifier
                        .weight(2f)
                        .padding(top = 9.dp),
                    thickness = 2.dp,
                    color = nextLineColor
                )
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 9.dp),
                    thickness = 2.dp,
                    color = extendRightColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                CompactStep(
                    state = if (previous != null) StepState.Completed else StepState.Hidden,
                    title = previous.orEmpty(),
                    modifier = Modifier.weight(1f)
                )
                CompactStep(
                    state = StepState.Current,
                    title = current,
                    bold = true,
                    modifier = Modifier.weight(1f)
                )
                CompactStep(
                    state = if (next != null) StepState.Pending else StepState.Hidden,
                    title = next.orEmpty(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Step ${currentStep + 1} of ${steps.size}",
            style = MaterialTheme.typography.labelMedium,
            color = DarkGreen
        )
    }
}

@Composable
private fun CompactStep(
    state: StepState,
    title: String,
    modifier: Modifier = Modifier,
    bold: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepCircle(state)

        Spacer(Modifier.height(4.dp))

        Crossfade(targetState = title, label = "compact-step-title") { animatedTitle ->
            Text(
                text = animatedTitle,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                color = DarkGreen
            )
        }
    }
}

@Composable
private fun SmallStepper(
    steps: List<WizardStep>,
    currentStep: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (currentStep + 1).toFloat() / steps.size.toFloat(),
        label = "small-stepper-progress"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Crossfade(
            targetState = "Step ${currentStep + 1} of ${steps.size}",
            label = "small-stepper-counter"
        ) { counter ->
            Text(
                text = counter,
                style = MaterialTheme.typography.labelMedium,
                color = DarkGreen
            )
        }

        Spacer(Modifier.height(4.dp))

        Crossfade(
            targetState = steps[currentStep].title,
            label = "small-stepper-title"
        ) { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }

        Spacer(Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth(),
            color = DarkGold,
            trackColor = LightGreen
        )
    }
}

private enum class StepState {
    Completed,
    Current,
    Pending,
    Hidden
}

@Composable
private fun StepCircle(
    state: StepState
) {
    val fill by animateColorAsState(
        targetValue = when (state) {
            StepState.Completed -> DarkGold
            StepState.Current -> DarkGreen
            StepState.Pending -> Color.White
            StepState.Hidden -> Color.Transparent
        },
        label = "step-circle-fill"
    )

    val border by animateColorAsState(
        targetValue = if (state == StepState.Pending) DarkGreen else Color.Transparent,
        label = "step-circle-border"
    )

    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(fill)
            .border(2.dp, border, CircleShape)
    )
}

private val previewSteps = listOf(
    WizardStep("Ad Type"),
    WizardStep("Details"),
    WizardStep("Images"),
    WizardStep("Review")
)

@Preview(name = "Full - wide", widthDp = 900, showBackground = true)
@Composable
private fun WizardProgressIndicatorFullPreview() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            WizardProgressIndicator(
                steps = previewSteps,
                currentStep = 1
            )
        }
    }
}

@Preview(name = "Compact - medium", widthDp = 500, showBackground = true)
@Composable
private fun WizardProgressIndicatorCompactPreview() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            WizardProgressIndicator(
                steps = previewSteps,
                currentStep = 1
            )
        }
    }
}

@Preview(name = "Small - narrow", widthDp = 320, showBackground = true)
@Composable
private fun WizardProgressIndicatorSmallPreview() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            WizardProgressIndicator(
                steps = previewSteps,
                currentStep = 2
            )
        }
    }
}