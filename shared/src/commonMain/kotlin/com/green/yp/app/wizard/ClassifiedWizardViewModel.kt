package com.green.yp.app.wizard

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.repository.ClassifiedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.Uuid

class ClassifiedWizardViewModel(
    private val repository: ClassifiedRepository
) : ViewModel(){

    private val log = Logger.withTag("green.yp.app.wizard.ClassifiedWizardViewModel")
    private val _state = MutableStateFlow(ClassifiedWizardState())

    val state: StateFlow<ClassifiedWizardState> =
        _state.asStateFlow()

    private inline fun updateState(
        block: ClassifiedWizardState.() -> ClassifiedWizardState
    ) {
        log.d("Updating state : "+_state.value.toString())
        _state.update(block)
    }

    /**
     * Public draft updater used by all wizard screens.
     */
    fun updateDraft(
        transform: (ClassifiedDraft) -> ClassifiedDraft
    ) {
        updateState {
            copy(
                draft = transform(draft)
            )
        }
    }

    // ------------------------
    // Draft Updates
    // ------------------------

    fun updateCategory(categoryId: Uuid) {
        updateDraft { draft ->
            draft.copy(categoryId = categoryId)
        }
    }

    fun updateAdType(adType: Uuid) {
        updateDraft { draft ->
            draft.copy(adType = adType)
        }
    }

    fun updatePrice(price: Double?) {
        updateDraft { draft ->
            draft.copy(price = price)
        }
    }

    fun updatePricePerUnitType(value: String) {
        updateDraft { draft ->
            draft.copy(pricePerUnitType = value)
        }
    }

    fun updateTitle(value: String) {
        updateDraft { draft ->
            draft.copy(title = value)
        }
    }

    fun updateDescription(value: String) {
        updateDraft { draft ->
            draft.copy(description = value)
        }
    }

    fun updateAddress(value: String) {
        updateDraft { draft ->
            draft.copy(address = value)
        }
    }

    fun updateCity(value: String) {
        updateDraft { draft ->
            draft.copy(city = value)
        }
    }

    fun updateStateCode(value: String) {
        updateDraft { draft ->
            draft.copy(state = value)
        }
    }

    fun updatePostalCode(value: String) {
        updateDraft { draft ->
            draft.copy(postalCode = value)
        }
    }

    fun updateFirstName(value: String) {
        updateDraft { draft ->
            draft.copy(firstName = value)
        }
    }

    fun updateLastName(value: String) {
        updateDraft { draft ->
            draft.copy(lastName = value)
        }
    }

    fun updatePhoneNumber(value: String) {
        updateDraft { draft ->
            draft.copy(phoneNumber = value)
        }
    }

    fun updateEmailAddress(value: String) {
        updateDraft { draft ->
            draft.copy(emailAddress = value)
        }
    }

    // ------------------------
    // Navigation
    // ------------------------

    fun previousStep() {
        updateState {
            copy(
                currentStep = currentStep.previous()
            )
        }
    }

    suspend fun nextStep() {

        if (!validateCurrentStep()) {
            return
        }

        when (_state.value.currentStep) {
            ClassifiedWizardStep.CONTACT -> {
                createClassifiedAd()
            }

            else -> {
                moveToNextStep()
            }
        }
    }

    private fun moveToNextStep() {

        updateState {
            copy(
                currentStep = currentStep.next()
            )
        }
    }

    // ------------------------
    // Validation
    // ------------------------

    private fun validateCurrentStep(): Boolean {
        val draft = _state.value.draft
        log.d("Validating current step - ${_state.value.currentStep}")
        return when (_state.value.currentStep) {
            ClassifiedWizardStep.PACKAGE -> draft.adType != null
            ClassifiedWizardStep.DETAILS -> draft.title.isNotBlank() && draft.description.isNotBlank()
            ClassifiedWizardStep.LOCATION ->
                draft.address.isNotBlank() && draft.city.isNotBlank() && draft.state.isNotBlank()
            ClassifiedWizardStep.CONTACT ->
                draft.firstName.isNotBlank() && draft.lastName.isNotBlank()
                        && draft.emailAddress.isNotBlank() && draft.phoneNumber.isNotBlank()
            else -> true
        }
    }

    // ------------------------
    // Server Actions
    // ------------------------

    private suspend fun createClassifiedAd() {
        log.d("Creating classified ad - ${_state.value.draft}")
        updateState {
            copy(
                loading = true,
                error = null
            )
        }

        try {
            val result =
                repository.createClassifiedAd(
                    buildRequest()
                )

            result.onSuccess { classified ->
                updateState {
                    copy(
                        loading = false,
                        listingId = classified.classifiedId,
                        currentStep =
                            ClassifiedWizardStep.EMAIL_VALIDATION
                    )
                }
            }.onFailure { exception ->
                updateState {
                    copy(
                        loading = false,
                        error = exception.message
                    )
                }
            }

        } catch (e: Exception) {

            updateState {
                copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun buildRequest(): ClassifiedRequest {

        val draft = _state.value.draft

        return ClassifiedRequest(
            adType = requireNotNull(draft.adType),
            categoryId = requireNotNull(draft.categoryId),
            price = requireNotNull(draft.price),
            pricePerUnitType =
                requireNotNull(draft.pricePerUnitType),

            firstName = draft.firstName,
            lastName = draft.lastName,

            address = draft.address,
            city = draft.city,
            state = draft.state,
            postalCode = draft.postalCode,

            phoneNumber = draft.phoneNumber,
            emailAddress = draft.emailAddress,

            title = draft.title,
            description = draft.description
        )
    }
}