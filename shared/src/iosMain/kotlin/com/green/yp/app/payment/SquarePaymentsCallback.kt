package com.green.yp.app.payment

interface SquarePaymentsCallback {
    fun onSuccess(token: String)
    fun onFailure(message: String)
}