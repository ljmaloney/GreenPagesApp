package com.green.yp.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import greenpagesapp.shared.generated.resources.Res
import greenpagesapp.shared.generated.resources.green_pages_loading_splash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

val defaultBusinesses: List<String> = listOf(
    "Classifieds",
    "Cleanup & Debris Removal",
    "Forestry Mulching",
    "Garden Center",
    "Hardscape Supplier",
    "Hauling",
    "Irrigation",
    "Landscaping",
    "Landscape Supplier",
    "Lawn Care",
    "Nurseries",
    "Ponds & Water Features",
    "Tree Services"
)

@Composable
fun LoadingScreen(
    businesses: List<String> = defaultBusinesses,
    onLoadingComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val backgroundColor = Color.White
    val textColor = Color(0xFF166534)
    val statusColor = Color(0xFF4B5563)

    var currentBusiness by remember { mutableStateOf(businesses.first()) }
    var statusLocation by remember { mutableStateOf("📍 Acquiring location...") }
    var statusApi by remember { mutableStateOf("🌱 Loading listings...") }

    var alpha by remember { mutableStateOf(1f) }
    val animatedAlpha by animateFloatAsState(targetValue = alpha)

    LaunchedEffect(Unit) {
        val shuffled = businesses.shuffled()
        var index = 0
        val job = scope.launch {
            while (index < shuffled.size) {
                currentBusiness = shuffled[index]
                alpha = 0f
                delay(150)
                alpha = 1f
                index++
                delay(650)
            }
        }

        delay(1500)
        statusLocation = "📍 Location acquired"
        delay(1500)
        statusApi = "🌱 Listings loaded"

        job.cancel()
        delay(500)
        onLoadingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(Res.drawable.green_pages_loading_splash),
                contentDescription = "Green Pages Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .scale(1.0f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            BasicText(
                text = currentBusiness,
                modifier = Modifier.alpha(animatedAlpha),
                style = TextStyle(
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            BasicText(
                text = statusLocation,
                style = TextStyle(
                    color = statusColor,
                    fontSize = 16.sp
                )
            )
            BasicText(
                text = statusApi,
                style = TextStyle(
                    color = statusColor,
                    fontSize = 16.sp
                )
            )
        }
    }
}