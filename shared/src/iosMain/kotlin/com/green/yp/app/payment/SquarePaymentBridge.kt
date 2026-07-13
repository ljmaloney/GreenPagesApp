package com.green.yp.app.payment

interface SquarePaymentBridge {
    fun startPayment(
        amount: Long,
        currency: String,
        onResult: (token: String?, error: String?) -> Unit
    )
}
