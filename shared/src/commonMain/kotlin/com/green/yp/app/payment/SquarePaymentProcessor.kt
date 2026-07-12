package com.green.yp.app.payment

expect class SquarePaymentProcessor {
    fun startPayment(
        amount: Long,
        currency: String,
        onResult: (PaymentResult) -> Unit
    )
}