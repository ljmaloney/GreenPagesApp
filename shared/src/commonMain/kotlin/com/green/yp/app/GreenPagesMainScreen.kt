package com.green.yp.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.green.yp.app.components.GreenPagesBottomBar
import com.green.yp.app.components.GreenPagesTopBar
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.screens.HomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Preview
@Composable
fun GreenPagesMainScreen(viewModel: ClassifiedViewModel = koinViewModel()) {
    var selectedTab by remember { mutableStateOf(0) }

    MaterialTheme {
        Scaffold(
            topBar = {
                GreenPagesTopBar(
                    onSearchClick = { /* TODO: Search action */ }
                )
            },
            bottomBar = {
                GreenPagesBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { paddingValues ->
            when (selectedTab) {
                0 -> HomeScreen(paddingValues, viewModel)
                else -> {
                    // TODO: Other screens
                    Column(modifier = Modifier.padding(paddingValues)) {
                        Text("Tab $selectedTab Content")
                    }
                }
            }
        }
    }
}
