package com.green.yp.app.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.green.yp.app.shared.dto.message.MessageRequest
import com.green.yp.app.shared.dto.message.MessageRequestType
import com.green.yp.app.shared.dto.message.ProfessionalMessageType
import com.green.yp.app.shared.dto.message.ProfessionalLeadRequest
import com.green.yp.app.shared.dto.search.SearchRecordType
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.repository.ClassifiedRepository
import com.green.yp.app.shared.repository.EmailContactRepository
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessagingViewModel(
    private val classified: ClassifiedRepository,
    private val messagingRepository: EmailContactRepository
) : ViewModel() {
    companion object {
        private const val SEND_MESSAGE_GENERIC_ERROR = "There was an error sending your message. Please try again later"
    }
    private val log = Logger.withTag("green.yp.app.messaging.MessagingViewModel")

    private val _state = MutableStateFlow(
        MessageDraft(
            emailAddress = "",
            name = "",
            phoneNumber = "",
            subject = "",
            message = ""
        )
    )
    val state: StateFlow<MessageDraft> = _state.asStateFlow()

    private inline fun updateState(
        block: MessageDraft.() -> MessageDraft
    ) {
        log.d("Updating message state: ${_state.value}")
        _state.update(block)
    }

    fun updateDraft(
        transform: (MessageDraft) -> MessageDraft
    ) {
        updateState {
            val newDraft = transform(this)
            newDraft.copy(
                companyName = newDraft.companyName?.trimEnd(),
                emailAddress = newDraft.emailAddress.trimEnd(),
                name = newDraft.name.trimEnd(),
                phoneNumber = newDraft.phoneNumber.trimEnd(),
                subject = newDraft.subject.trimEnd(),
                message = newDraft.message.trimEnd()
            )
        }
    }

    fun sendContactMessage(
        searchResponse: SearchResponseDTO,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        initializeClassifiedDraft(searchResponse)
        when (searchResponse.recordType) {
            SearchRecordType.GREEN_PRO -> {
                sendContactMessage(
                    messageType = MessageRequestType.PRODUCER_GENERIC_TYPE,
                    proMessage = buildProfessionalLeadRequest(searchResponse),
                    onResult = onResult
                )
            }

            else -> {
                sendContactMessage(
                    messageType = MessageRequestType.CLASSIFIED_AD_EMAIL,
                    classifiedId = searchResponse.externId,
                    onResult = onResult
                )
            }
        }
    }

    private fun buildProfessionalLeadRequest(searchResponse: SearchResponseDTO): ProfessionalLeadRequest {
        return ProfessionalLeadRequest(
            producerId = searchResponse.producerId.orEmpty(),
            locationId = searchResponse.locationId.orEmpty(),
            contactType = ProfessionalMessageType.GENERAL_REQUEST,
            productServiceRef = searchResponse.categoryRef
        )
    }

    fun initializeClassifiedDraft(searchResponse: SearchResponseDTO) {
        val defaultSubject = "Re: ${searchResponse.title}"
        val defaultPhone = classified.createdAd.value?.phoneNumber
            ?.takeIf { it.isNotBlank() }
            ?: searchResponse.phoneNumber?.takeIf { it.isNotBlank() }

        updateState {
            copy(
                phoneNumber = phoneNumber.ifBlank { defaultPhone.orEmpty() },
                subject = subject.ifBlank { defaultSubject }
            )
        }
    }

    fun sendContactMessage(
        messageType: MessageRequestType,
        classifiedId: String? = null,
        proMessage: ProfessionalLeadRequest? = null,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        val draft = _state.value
        val request = MessageRequest(
            requestType = messageType,
            classifiedRequest = classifiedId?.let {
                com.green.yp.app.shared.dto.message.ClassifiedRequest(classifiedId = it)
            },
            leadContactRequest = proMessage,
            companyName = draft.companyName,
            emailAddress = draft.emailAddress,
            name = draft.name,
            phoneNumber = formatPhoneForSubmission(draft.phoneNumber),
            subject = draft.subject,
            message = draft.message
        )
        log.d("Sending contact message: $request")

        viewModelScope.launch {
            val result = messagingRepository.sendContactMessage(request)
            result.onFailure { throwable ->
                log.e("Failed sending contact message: ${throwable.message ?: "Unknown error"}")
            }
            onResult(result)
        }
    }

    fun getSendMessageError(throwable: Throwable): String {
        val responseError = throwable as? ResponseException
        val statusCode = responseError?.response?.status?.value

        return when {
            statusCode in 400..499 -> throwable.message
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: SEND_MESSAGE_GENERIC_ERROR

            statusCode in 500..599 -> SEND_MESSAGE_GENERIC_ERROR

            else -> throwable.message
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: SEND_MESSAGE_GENERIC_ERROR
        }
    }

    private fun formatPhoneForSubmission(input: String): String {
        val digits = input.filter { it.isDigit() }
        val normalizedDigits = when {
            digits.length == 11 && digits.startsWith("1") -> digits.drop(1)
            digits.length == 10 -> digits
            else -> return input
        }

        return "(${normalizedDigits.substring(0, 3)}) ${normalizedDigits.substring(3, 6)}-${normalizedDigits.substring(6, 10)}"
    }
}