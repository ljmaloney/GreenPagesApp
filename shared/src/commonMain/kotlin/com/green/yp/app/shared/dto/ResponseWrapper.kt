package com.green.yp.app.shared.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseWrapper<T>(
    @SerialName("response")
    val response: List<T>,
    @SerialName("errorMessageApi")
    val errorMessageApi: ErrorMessageApi
)

@Serializable
data class ErrorMessageApi(
    @SerialName("errorCode")
    val errorCode: String,
    @SerialName("displayMessage")
    val displayMessage: String,
    @SerialName("errorDetails")
    val errorDetails: String
)