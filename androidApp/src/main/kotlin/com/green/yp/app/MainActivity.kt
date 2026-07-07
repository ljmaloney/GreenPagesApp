package com.green.yp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.green.yp.app.media.AndroidImagePicker
import com.green.yp.app.media.LocalImagePicker
import com.green.yp.app.shared.di.KoinInitializer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        KoinInitializer.init()
        setContent {
            val imagePicker = remember { AndroidImagePicker(this) }
            CompositionLocalProvider(LocalImagePicker provides imagePicker) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}