package com.green.yp.app.shared.dto.message

import kotlinx.serialization.Serializable

enum class MessageRequestType(
    val description: String,
    val messageType: String
) {
    CLASSIFIED_AD_EMAIL("Customer contacting classified ad customer", "classifiedMessage"),
    SUBSCRIBER_INFO_TYPE("Subscriber contact request", "noEmail"),
    SUBSCRIBER_SUPPORT_TYPE("Subscriber support contact request", "supportMessage"),
    PRODUCER_GENERIC_TYPE("Generic producer contact request", "genericMessage"),
    PRODUCER_PRODUCT_TYPE("Contact regarding specific product", "productMessage"),
    PRODUCER_SERVICE_TYPE("Contact regarding specific service", "serviceMessage");
}

enum class ProfessionalMessageType(
    val description: String
) {
    GENERAL_REQUEST("General Contact Request"),
    PRODUCT_REQUEST("Product Contact Request"),
    SERVICE_REQUEST("Service Contact Request");
}

@Serializable
data class MessageRequest(
    val requestType: MessageRequestType,
    val classifiedRequest: ClassifiedRequest? = null,
    val leadContactRequest: ProfessionalLeadRequest? = null,
    val companyName: String? = null,
    val emailAddress: String,
    val name: String,
    val phoneNumber: String,
    val subject: String,
    val message: String
)

@Serializable
data class ClassifiedRequest(
    val classifiedId: String
)

@Serializable
data class ProfessionalLeadRequest(
    val producerId: String,
    val locationId: String,
    val contactType: ProfessionalMessageType,
    val productServiceRef: String
)
