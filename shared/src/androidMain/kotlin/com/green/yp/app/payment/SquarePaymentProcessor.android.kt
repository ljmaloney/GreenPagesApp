package com.green.yp.app.payment

actual class SquarePaymentProcessor {

    actual fun startPayment(
        amount: Long,
        currency: String,
        onResult: (PaymentResult) -> Unit
    ) {
        onResult(
            PaymentResult.Failure(
                "Android Square payment integration not implemented"
            )
        )
    }
}