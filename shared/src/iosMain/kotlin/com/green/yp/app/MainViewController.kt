package com.green.yp.app

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.green.yp.app.shared.di.KoinInitializer

fun MainViewController() = ComposeUIViewController { RootContent() }

fun initKoin() {
    KoinInitializer.init()
}

@Composable
private fun RootContent() {
    var loading by remember { mutableStateOf(true) }
    if (loading) {
        LoadingScreen(onLoadingComplete = { loading = false })
    } else {
        App()
    }
}