package com.green.yp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.green.yp.app.Greeting
import com.green.yp.app.getLocationManager
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import greenpagesapp.shared.generated.resources.Res
import greenpagesapp.shared.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    viewModel: ClassifiedViewModel
) {
    val locationManager = remember { getLocationManager() }
    val userLocation by locationManager.locationUpdates.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showContent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(paddingValues)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Content below menu bar
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), contentDescription = null)
                categories.firstOrNull()?.let { firstCategory ->
                    Text(
                        text = "First Category: ${firstCategory.name}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                userLocation?.let { location ->
                    Text(
                        text = "Your Location: ${location.latitude}, ${location.longitude}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } ?: Text(
                    text = "Location not available",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text("Compose: $greeting")
            }
        }
    }
}
