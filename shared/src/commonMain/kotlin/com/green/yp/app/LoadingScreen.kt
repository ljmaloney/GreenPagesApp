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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import greenpagesapp.shared.generated.resources.Res
import greenpagesapp.shared.generated.resources.greenyp_splash_screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

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
    viewModel: ClassifiedViewModel = koinViewModel(),
    onLoadingComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val locationManager = getLocationManager()
    val currentLocation = locationManager.locationUpdates.collectAsState()

    val categories by viewModel.categories.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Start updates immediately only when location permission/services are already available.
    // If permissions are granted later (e.g. via Activity), MainActivity will call startLocationUpdates()

    val backgroundColor = Color.White
    val textColor = Color(0xFF166534)
    val statusColor = Color(0xFF4B5563)

    var currentBusiness by remember { mutableStateOf(businesses.first()) }
    var statusLocation by remember { mutableStateOf("📍 Acquiring location...") }
    var statusApi by remember { mutableStateOf("🌱 Loading listings...") }

    var alpha by remember { mutableStateOf(1f) }
    val animatedAlpha by animateFloatAsState(targetValue = alpha)

    LaunchedEffect(currentLocation.value) {
        if (currentLocation.value != null) {
            statusLocation = "📍 Location acquired"
            println("DEBUG: LoadingScreen: Location acquired from StateFlow")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
        val shuffled = businesses.shuffled()
        var index = 0

        if (locationManager.isLocationAvailable()) {
            println("DEBUG: LoadingScreen: Starting location updates")
            locationManager.startLocationUpdates()
        } else {
            println("DEBUG: LoadingScreen: Location not available")
        }

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

        // Wait for categories to be loaded or an error to occur
        while (categories.isEmpty() && errorMessage == null) {
            delay(500)
        }

        if (errorMessage != null) {
            // Show error message in UI and do not proceed to main screen
            statusApi = "⚠️ ${errorMessage}"
            job.cancel()
            return@LaunchedEffect
        }

        delay(1500)
        // statusLocation is now handled by the observer LaunchedEffect above
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
                painter = painterResource(Res.drawable.greenyp_splash_screen),
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
            currentLocation.value?.let {location ->
                Text("Lat: Location Found")
            }?:run{
                BasicText(
                    text = statusLocation,
                    style = TextStyle(
                        color = statusColor,
                        fontSize = 16.sp
                    )
                )
            }

            BasicText(
                text = statusApi,
                style = TextStyle(
                    color = statusColor,
                    fontSize = 16.sp
                )
            )
        }
        // If there's an error, show a dedicated ErrorScreen (reusable composable)
        errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            ErrorScreen(message = msg, onRetry = { viewModel.retry() }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp))
        }
    }
}