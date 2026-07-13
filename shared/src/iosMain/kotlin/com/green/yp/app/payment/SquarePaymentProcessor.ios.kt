package com.green.yp.app.payment

import platform.darwin.NSObject

actual class SquarePaymentProcessor {

    actual fun startPayment(
        amount: Long,
        currency: String,
        onResult: (PaymentResult) -> Unit
    ) {
        SquarePaymentNative.startPayment(
            amount,
            currency,
            onResult
        )
    }
}