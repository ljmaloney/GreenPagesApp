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
import androidx.compose.foundation.layout.fillMaxSize
import com.green.yp.app.components.GreenPagesBottomBar
import com.green.yp.app.components.GreenPagesTopBar
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import com.green.yp.app.screens.ExploreMarketResultsScreen
import com.green.yp.app.screens.SearchScreen
import org.koin.compose.viewmodel.koinViewModel

@Preview
@Composable
fun GreenPagesMainScreen(
    searchViewModel: SearchViewModel = koinViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }

    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                GreenPagesTopBar(
                    onSearchClick = { selectedTab = 1 }
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
                0 -> ExploreMarketResultsScreen(paddingValues, viewModel = searchViewModel)
                1 -> SearchScreen(paddingValues = paddingValues)
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
