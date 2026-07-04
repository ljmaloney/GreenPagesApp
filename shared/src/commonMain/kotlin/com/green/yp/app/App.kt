package com.green.yp.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.green.yp.app.enum.AppStateType
import com.green.yp.app.wizard.GreenPagesClassifiedWizard

@Composable
fun App() {
    MaterialTheme {
        var state by remember {
            mutableStateOf(AppStateType.LOADING)
        }
        var showWizard by remember { mutableStateOf(false) }
        
        when (state) {

            AppStateType.LOADING ->
                LoadingScreen(onLoadingComplete = { state = AppStateType.READY })

            AppStateType.READY -> {
                if (showWizard) {
                    GreenPagesClassifiedWizard(
                        onBackClick = { showWizard = false }
                    )
                } else {
                    GreenPagesMainScreen(
                        onNavigateToWizard = { showWizard = true }
                    )
                }
            }

            AppStateType.ERROR ->
                ErrorScreen()
        }
    }
}

@Composable
fun ErrorScreen() {
    TODO("Not yet implemented")
}