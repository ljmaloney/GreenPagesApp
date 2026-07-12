package com.green.yp.app.payment

import platform.Foundation.NSLog

internal object SquarePaymentNative {

    fun startPayment(
        amount: Long,
        currency: String,
        onResult: (PaymentResult) -> Unit
    ) {
        NSLog("Square payment start: amount=$amount currency=$currency")

        onResult(
            PaymentResult.Failure(
                "Square iOS native implementation not connected yet"
            )
        )
    }
}
