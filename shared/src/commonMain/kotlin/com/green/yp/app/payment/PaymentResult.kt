package com.green.yp.app.payment

sealed interface PaymentResult {
    data class Success(
        val token: String
    ) : PaymentResult

    data class Failure(
        val message: String
    ) : PaymentResult
}