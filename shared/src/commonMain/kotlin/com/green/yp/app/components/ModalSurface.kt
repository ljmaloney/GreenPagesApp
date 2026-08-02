package com.green.yp.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.green.yp.app.ui.theme.Gray400
import com.green.yp.app.ui.theme.Gold500
import com.green.yp.app.ui.theme.Green700

@Composable
fun ModalSurface(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    widthFraction: Float = 0.92f,
    maxHeight: Dp = 700.dp,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(28.dp),
    containerColor: Color = Color.White,
    tonalElevation: Dp = 8.dp,
    header: String = "",
    content: @Composable BoxScope.() -> Unit
) {
    if (!visible) return

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        enabled = dismissOnClickOutside,
                        onClick = onDismissRequest
                    ),
                contentAlignment = Alignment.TopCenter
            ) {

                Surface(
                    modifier = modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(widthFraction)
                        .heightIn(max = maxHeight)
                        .clickable(enabled = false) { },
                    shape = shape,
                    color = containerColor,
                    tonalElevation = tonalElevation
                ) {

                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-6).dp)
                                .zIndex(1f),
                            verticalAlignment = Alignment.Top
                        ) {
                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier.offset(x = (-6).dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Gold500,
                                    contentColor = Color.Red
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close"
                                )
                            }

                            Text(
                                text = header,
                                style = MaterialTheme.typography.titleMedium,
                                color = Green700,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp, top = 8.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .verticalScroll(scrollState)
                                .drawVerticalScrollbar(scrollState)
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.drawVerticalScrollbar(
    state: androidx.compose.foundation.ScrollState,
    color: Color = Gray400
): Modifier = drawWithContent {
    drawContent()

    val viewPortHeight = size.height
    val totalContentHeight = state.maxValue + viewPortHeight
    val scrollValue = state.value.toFloat()

    if (totalContentHeight > viewPortHeight) {
        val scrollbarHeight = (viewPortHeight / totalContentHeight) * viewPortHeight
        val scrollbarOffset = (scrollValue / totalContentHeight) * viewPortHeight

        drawRect(
            color = color.copy(alpha = 0.5f),
            topLeft = Offset(size.width - 4.dp.toPx(), scrollbarOffset),
            size = Size(4.dp.toPx(), scrollbarHeight)
        )
    }
}

@Preview
@Composable
private fun ModalSurfacePreview() {
    var visible by remember { mutableStateOf(true) }

    ModalSurface(
        visible = visible,
        onDismissRequest = { visible = false },
        header = "Scrollable Modal Preview"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(50) { index ->
                Text(
                    text = "Item #$index - This is some long content to trigger the scrollbar in the modal surface.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
