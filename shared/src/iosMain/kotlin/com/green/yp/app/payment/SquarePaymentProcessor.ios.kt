package com.green.yp.app.payment

actual class SquarePaymentProcessor {

    actual fun startPayment(
        amount: Long,
        currency: String,
        onResult: (PaymentResult) -> Unit
    ) {
        // call Swift bridge here
    }
}