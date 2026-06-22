package com.green.yp.app

import android.app.Application
import com.green.yp.app.shared.di.KoinInitializer

class GreenPagesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer.init()
    }
}
