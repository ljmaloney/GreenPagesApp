package com.green.yp.app.shared.di

import com.green.yp.app.payment.SquarePaymentProcessor
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Add iOS specific bindings here
    factory { SquarePaymentProcessor() }
}
