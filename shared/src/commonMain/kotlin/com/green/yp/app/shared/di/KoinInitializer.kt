package com.green.yp.app.shared.di

import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools

object KoinInitializer {
    fun init() {
        if (KoinPlatformTools.defaultContext().getOrNull() != null) return

        startKoin {
            modules(
                platformModule(),
                appModule
            )
        }
    }
}

fun initKoin() = KoinInitializer.init()
