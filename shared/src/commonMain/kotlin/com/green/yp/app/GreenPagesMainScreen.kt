package com.green.yp.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.ui.theme.DarkGreen
import org.jetbrains.compose.resources.painterResource
import greenpagesapp.shared.generated.resources.Res
import greenpagesapp.shared.generated.resources.compose_multiplatform
import greenpagesapp.shared.generated.resources.green_pages_app_banner
import org.koin.compose.viewmodel.koinViewModel

data class NavItem(val label: String, val icon: ImageVector)

@Preview
@Composable
fun GreenPagesMainScreen(viewModel: ClassifiedViewModel = koinViewModel()) {
    val locationManager = remember { getLocationManager() }
    val userLocation by locationManager.locationUpdates.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Explore", Icons.Default.Search),
        NavItem("Create", Icons.Default.AddCircle),
        NavItem("Messages", Icons.Default.Email),
        NavItem("Profile", Icons.Default.Person)
    )

    MaterialTheme {
        Scaffold(
            topBar = {
                // Menu Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.green_pages_app_banner),
                            contentDescription = "Green Pages Banner",
                            modifier = Modifier.height(40.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { /* TODO: Search action */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Black
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = DarkGreen
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = DarkGreen,
                    windowInsets = WindowInsets(0.dp)
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (selectedTab == index) DarkGreen else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    color = if (selectedTab == index) DarkGreen else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
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
    }
}
