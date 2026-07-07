package com.green.yp.app.shared.api

import com.green.yp.app.shared.dto.email.EmailValidationRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface EmailContactApi {
    @POST("email/validate")
    suspend fun validateEmail(@Body emailValidationRequest: EmailValidationRequest)
}
