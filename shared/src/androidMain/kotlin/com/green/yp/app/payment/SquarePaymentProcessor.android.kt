package com.green.yp.app.payment

import android.app.Activity
import android.content.Intent
import sqip.Callback
import sqip.CardDetails
import sqip.CardEntry
import sqip.CardEntryActivityCommand
import sqip.CardEntryActivityResult
import sqip.CardNonceBackgroundHandler

actual class SquarePaymentProcessor(private val activity: Activity) {

    actual fun startPayment(
        amount: Long,
        currency: String,
        onResult: (PaymentResult) -> Unit
    ) {
        currentCallback = onResult

        CardEntry.setCardNonceBackgroundHandler(object : CardNonceBackgroundHandler {
            override fun handleEnteredCardInBackground(cardDetails: CardDetails): CardEntryActivityCommand {
                return try {
                    // Return the nonce to the common code via the callback
                    onResult(PaymentResult.Success(cardDetails.nonce))
                    currentCallback = null
                    CardEntryActivityCommand.Finish()
                } catch (e: Exception) {
                    val errorMessage = e.message ?: "Unknown error during payment"
                    onResult(PaymentResult.Failure(errorMessage))
                    currentCallback = null
                    CardEntryActivityCommand.ShowError(errorMessage)
                }
            }
        })

        CardEntry.startCardEntryActivity(activity)
    }

    companion object {
        private var currentCallback: ((PaymentResult) -> Unit)? = null

        fun handleActivityResult(data: Intent?) {
            CardEntry.handleActivityResult(data, object : Callback<CardEntryActivityResult> {
                override fun onResult(result: CardEntryActivityResult) {
                    if (result.isCanceled()) {
                        currentCallback?.invoke(PaymentResult.Failure("Payment cancelled by user"))
                        currentCallback = null
                    }
                }
            })
        }
    }
}
