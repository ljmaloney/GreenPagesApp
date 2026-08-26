package com.green.yp.app.shared.dto.message

data class MessageDTO(val companyName: String? = null,
                      val emailAddress: String,
                      val name: String,
                      val phoneNumber: String,
                      val subject: String,
                      val message: String)