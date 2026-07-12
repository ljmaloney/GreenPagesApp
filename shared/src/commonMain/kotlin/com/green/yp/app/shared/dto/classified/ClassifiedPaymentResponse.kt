package com.green.yp.app.shared.dto.classified

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ClassifiedPaymentResponse(
    @SerialName("classifiedId")
    val classifiedId: Uuid,
    @SerialName("classifiedTitle")
    val classifiedTitle: String,
    @SerialName("paymentStatus")
    val paymentStatus: String,
    @SerialName("paymentRef")
    val paymentRef: String,
    @SerialName("orderRef")
    val orderRef: String,
    @SerialName("receiptNumber")
    val receiptNumber: String,
    @SerialName("errorStatusCode")
    val errorStatusCode: String,
    @SerialName("errorDetail")
    val errorDetail: String
)
