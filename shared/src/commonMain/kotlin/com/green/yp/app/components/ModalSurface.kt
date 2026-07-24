package com.green.yp.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.green.yp.app.ui.theme.DarkGold

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
    content: @Composable BoxScope.() -> Unit
) {
    if (!visible) return

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

                    Box(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(y = (-8).dp)
                                .zIndex(1f),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = DarkGold,
                                contentColor = Color.Red
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }

                        Box(modifier = Modifier.padding(top = 52.dp)) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ModalSurfacePreview() {
    var visible by remember { mutableStateOf(true) }

    ModalSurface(
        visible = visible,
        onDismissRequest = { visible = false }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Modal content preview",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
