package com.green.yp.app.shared.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools

object KoinInitializer {
    fun init(config: KoinAppDeclaration? = null) {
        if (KoinPlatformTools.defaultContext().getOrNull() != null) return

        startKoin {
            config?.invoke(this)
            modules(
                platformModule(),
                appModule
            )
        }
    }
}

fun initKoin() = KoinInitializer.init()
