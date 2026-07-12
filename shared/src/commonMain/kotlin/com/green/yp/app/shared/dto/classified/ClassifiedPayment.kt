package com.green.yp.app.shared.dto.classified

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ClassifiedPayment(
    @SerialName("referenceId")
    val referenceId: Uuid,
    @SerialName("paymentToken")
    val paymentToken: String,
    @SerialName("verificationToken")
    val verificationToken: String,
    @SerialName("emailValidationToken")
    val emailValidationToken: String,
    @SerialName("companyName")
    val companyName: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("addressLine1")
    val addressLine1: String,
    @SerialName("addressLine2")
    val addressLine2: String,
    @SerialName("city")
    val city: String,
    @SerialName("state")
    val state: String,
    @SerialName("postalCode")
    val postalCode: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("emailAddress")
    val emailAddress: String,
    @SerialName("producerPayment")
    val producerPayment: ProducerPayment
)

@Serializable
data class ProducerPayment(
    @SerialName("paymentMethod")
    val paymentMethod: String,
    @SerialName("actionType")
    val actionType: String,
    @SerialName("cycleType")
    val cycleType: String
)
