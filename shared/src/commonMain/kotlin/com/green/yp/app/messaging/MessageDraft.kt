package com.green.yp.app.messaging

data class MessageDraft(val companyName: String? = null,
                        val emailAddress: String,
                        val name: String,
                        val phoneNumber: String,
                        val subject: String,
                        val message: String)
