package com.green.yp.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.green.yp.app.enum.AppStateType

@Composable
@Preview
fun App() {
    MaterialTheme {
        var state by remember {
            mutableStateOf(AppStateType.LOADING)
        }
        when(state) {

            AppStateType.LOADING ->
                LoadingScreen(onLoadingComplete = { state = AppStateType.READY })
            AppStateType.READY ->
                GreenPagesMainScreen()
            AppStateType.ERROR ->
                ErrorScreen()
        }
    }
}

@Composable
fun ErrorScreen() {
    TODO("Not yet implemented")
}