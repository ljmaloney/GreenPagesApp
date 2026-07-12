package com.green.yp.app

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.green.yp.app.media.IOSImagePicker
import com.green.yp.app.media.IOSPickerDelegate
import com.green.yp.app.media.LocalImagePicker

import com.green.yp.app.shared.di.KoinInitializer

fun MainViewController(pickerDelegate: IOSPickerDelegate) = ComposeUIViewController {
    val imagePicker = remember { IOSImagePicker(pickerDelegate) }
    CompositionLocalProvider(LocalImagePicker provides imagePicker) {
        RootContent() 
    }
}

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