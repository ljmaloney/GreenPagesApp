package com.green.yp.app.shared.di

import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

object KoinInitializer {
    fun init() {
        if (KoinPlatform.getKoinOrNull() == null) {
            startKoin {
                modules(
                    platformModule(),
                    appModule
                )
            }
        }
    }
}

fun initKoin() = KoinInitializer.init()
