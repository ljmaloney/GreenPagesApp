package com.green.yp.app.shared.dto.email

import kotlinx.serialization.Serializable

@Serializable
data class EmailValidationRequest(
    val externRef: String,
    val emailAddress: String,
    val token: String
)
