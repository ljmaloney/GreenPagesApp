package com.green.yp.app.payment

import platform.Foundation.NSLog

internal object SquarePaymentNative {
    private var bridge: SquarePaymentBridge? = null

    fun setBridge(bridge: SquarePaymentBridge) {
        this.bridge = bridge
    }

    fun startPayment(
        amount: Long,
        currency: String,
        onResult: (PaymentResult) -> Unit
    ) {
        NSLog("Square payment start: amount=$amount currency=$currency")
        
        val currentBridge = bridge
        if (currentBridge == null) {
            onResult(PaymentResult.Failure("Payment bridge not initialized"))
            return
        }

        currentBridge.startPayment(
            amount = amount,
            currency = currency
        ) { token, error ->

            if (token != null) {
                onResult(PaymentResult.Success(token))
            } else {
                onResult(
                    PaymentResult.Failure(
                        error ?: "Unknown payment error"
                    )
                )
            }
        }
    }
}
