package com.green.yp.app.shared.di

import com.green.yp.app.payment.SquarePaymentProcessor
import com.green.yp.app.shared.util.ActivityHolder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    factory { SquarePaymentProcessor(ActivityHolder.currentActivity!!) }
}