package com.green.yp.app.shared.di

import org.koin.core.context.startKoin

object KoinInitializer {
    fun init() {
        startKoin {
            modules(
                platformModule(),
                appModule
            )
        }
    }
}

fun initKoin() = KoinInitializer.init()
