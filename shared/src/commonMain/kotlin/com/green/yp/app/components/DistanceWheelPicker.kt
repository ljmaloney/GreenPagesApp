package com.green.yp.app.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swmansion.kmpwheelpicker.WheelPicker
import com.swmansion.kmpwheelpicker.rememberWheelPickerState
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.DarkGreen

@Composable
fun DistanceWheelPicker(
    selectedDistance: Float,
    onDistanceChange: (Float) -> Unit
) {
    // Use step values instead of every single mile (5, 10, 15... 100)
    val distanceValues = (1..20).map { it * 5 }
    val initialIndex = distanceValues.indexOfFirst { it >= selectedDistance.toInt() }
        .takeIf { it >= 0 } ?: 1
    
    val wheelState = rememberWheelPickerState(
        itemCount = distanceValues.size,
        initialIndex = initialIndex
    )

    LaunchedEffect(wheelState.index) {
        val newDistance = distanceValues[wheelState.index].toFloat()
        if (newDistance != selectedDistance) {
            onDistanceChange(newDistance)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Distance: ${distanceValues[wheelState.index]} miles",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkGold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        WheelPicker(
            modifier = Modifier
                .width(120.dp)
                .height(40.dp)
                .border(2.dp, DarkGreen, RoundedCornerShape(8.dp)),
            state = wheelState,
            bufferSize = 1
        ) { index ->
            Text(
                text = "${distanceValues[index]} mi",
                style = MaterialTheme.typography.labelSmall,
                color = if (index == wheelState.index) DarkGold else DarkGreen
            )
        }
    }
}
