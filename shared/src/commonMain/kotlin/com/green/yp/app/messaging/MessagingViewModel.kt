package com.green.yp.app.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.green.yp.app.shared.dto.message.MessageRequest
import com.green.yp.app.shared.dto.message.MessageRequestType
import com.green.yp.app.shared.dto.message.ProfessionalLeadRequest
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.repository.ClassifiedRepository
import com.green.yp.app.shared.repository.EmailContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessagingViewModel(
    private val classified: ClassifiedRepository,
    private val messagingRepository: EmailContactRepository
) : ViewModel() {
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
        sendContactMessage(
            messageType = MessageRequestType.CLASSIFIED_AD_EMAIL,
            classifiedId = searchResponse.externId,
            onResult = onResult
        )
    }

    fun initializeClassifiedDraft(searchResponse: SearchResponseDTO) {
        val defaultSubject = "Re: ${searchResponse.title}"
        val defaultPhone = classified.createdAd.value?.phoneNumber
            ?.takeIf { it.isNotBlank() }
            ?: searchResponse.phoneNumber.takeIf { it.isNotBlank() }

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
            phoneNumber = draft.phoneNumber,
            subject = draft.subject,
            message = draft.message
        )
        log.d("Sending contact message: $request")

        viewModelScope.launch {
            val result = messagingRepository.sendContactMessage(request)
            result.onFailure { throwable ->
                log.e("Failed sending contact message", throwable)
            }
            onResult(result)
        }
    }
}