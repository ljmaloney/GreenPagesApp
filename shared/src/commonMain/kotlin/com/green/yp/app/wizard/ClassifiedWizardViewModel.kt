package com.green.yp.app.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.green.yp.app.payment.PaymentResult
import com.green.yp.app.payment.SquarePaymentProcessor
import com.green.yp.app.shared.dto.classified.ClassifiedPayment
import com.green.yp.app.shared.dto.classified.ClassifiedRequest
import com.green.yp.app.shared.dto.classified.ProducerPayment
import com.green.yp.app.shared.repository.ClassifiedRepository
import com.green.yp.app.shared.viewmodel.EmailContactViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClassifiedWizardViewModel(
    private val repository: ClassifiedRepository,
    private val emailViewModel: EmailContactViewModel,
    private val paymentProcessor: SquarePaymentProcessor
) : ViewModel(){

    private val log = Logger.withTag("green.yp.app.wizard.ClassifiedWizardViewModel")
    private val _state = MutableStateFlow(ClassifiedWizardState())

    val state: StateFlow<ClassifiedWizardState> =
        _state.asStateFlow()

    init {
        // Synchronize state from emailViewModel to maintain unified wizard state
        emailViewModel.isValidated
            .onEach { validated -> updateState { copy(emailValidated = validated) } }
            .launchIn(viewModelScope)

        emailViewModel.isLoading
            .onEach { loading -> updateState { copy(loading = loading) } }
            .launchIn(viewModelScope)

        emailViewModel.errorMessage
            .onEach { error -> updateState { copy(error = error) } }
            .launchIn(viewModelScope)
    }

    private inline fun updateState(
        block: ClassifiedWizardState.() -> ClassifiedWizardState
    ) {
        log.d("Updating state : "+_state.value.toString())
        _state.update(block)
    }

    /**
     * Public draft updater used by all wizard screens.
     * Automatically trims trailing whitespace from all string fields.
     */
    fun updateDraft(
        transform: (ClassifiedDraft) -> ClassifiedDraft
    ) {
        updateState {
            val newDraft = transform(draft)
            copy(
                draft = newDraft.copy(
                    firstName = newDraft.firstName.trimEnd(),
                    lastName = newDraft.lastName.trimEnd(),
                    address = newDraft.address.trimEnd(),
                    city = newDraft.city.trimEnd(),
                    state = newDraft.state.trimEnd(),
                    postalCode = newDraft.postalCode.trimEnd(),
                    phoneNumber = newDraft.phoneNumber.trimEnd(),
                    emailAddress = newDraft.emailAddress.trimEnd(),
                    title = newDraft.title.trimEnd(),
                    description = newDraft.description.trimEnd(),
                    pricePerUnitType = newDraft.pricePerUnitType?.trimEnd()
                )
            )
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
    // Server Actions
    // ------------------------
    fun validateEmail(token: String) {
        val currentState = _state.value
        val listingId = currentState.listingId?.toString() ?: return
        val email = currentState.draft.emailAddress
        
        // Delegate to emailViewModel which uses the correct /email/validate endpoint
        emailViewModel.validateEmail(listingId, email, token)
    }

    fun resetEmailValidationState() {
        emailViewModel.resetValidationState()
    }

    fun clearEmailError() {
        emailViewModel.clearError()
    }

    fun startPaymentFlow(
        amount: Long,
        currency: String,
        emailValidationToken: String
    ) {
        paymentProcessor.startPayment(
            amount = amount,
            currency = currency
        ) { result: PaymentResult ->
            log.d("Payment result: $result")
            when (result) {
                is PaymentResult.Success -> {
                    updateState {
                        copy(
                            currentStep = ClassifiedWizardStep.PAYMENT,
                            loading = true,
                            error = null
                        )
                    }
                    viewModelScope.launch {
                        processPayment(result.token, emailValidationToken)
                    }
                }
                is PaymentResult.Failure -> {
                    updateState { copy(error = result.message) }
                }
            }
        }
    }

    private suspend fun processPayment(paymentToken: String, emailValidationToken: String) {
        val currentState = _state.value
        val listingId = currentState.listingId ?: return

        updateState { copy(loading = true, error = null) }

        val payment = ClassifiedPayment(
            referenceId = listingId,
            paymentToken = paymentToken,
            verificationToken = "", // Optional/Future
            emailValidationToken = emailValidationToken,
            companyName = "",
            firstName = currentState.draft.firstName,
            lastName = currentState.draft.lastName,
            addressLine1 = currentState.draft.address,
            addressLine2 = "",
            city = currentState.draft.city,
            state = currentState.draft.state,
            postalCode = currentState.draft.postalCode,
            phoneNumber = currentState.draft.phoneNumber,
            emailAddress = currentState.draft.emailAddress,
            producerPayment = ProducerPayment(
                paymentMethod = "CHARGE",
                actionType = "APPLY_ONCE",
                cycleType = "MONTHLY"
            )
        )
        log.d("Processing payment for classified ad: $payment.listingId")
        val result = repository.processClassifiedPayment(payment)

        result.onSuccess { response ->
            if ( response.paymentStatus == "SUCCESS" ){
                updateState {
                    copy(
                        loading = false,
                        paymentResponse = response,
                        currentStep = ClassifiedWizardStep.PAYMENT_SUCCESS
                    )
                }
            }
            updateState {
                copy(
                    loading = false,
                    paymentResponse = response,
                    currentStep = ClassifiedWizardStep.PAYMENT_FAILED
                )
            }
        }.onFailure { exception ->
            updateState {
                copy(
                    loading = false,
                    error = exception.message,
                    currentStep = ClassifiedWizardStep.PAYMENT_FAILED
                )
            }
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
                            ClassifiedWizardStep.EMAIL_VALIDATION,
                        classifiedResponse = classified
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
            adType = requireNotNull(draft.adType) { "Ad type is missing" },
            categoryId = requireNotNull(draft.categoryId) { "Category is missing" },
            price = draft.price ?: 0.0,
            pricePerUnitType = draft.pricePerUnitType ?: "",

            firstName = draft.firstName,
            lastName = draft.lastName,

            address = draft.address,
            city = draft.city,
            state = draft.state,
            postalCode = draft.postalCode,

            phoneNumber = draft.phoneNumber,
            emailAddress = draft.emailAddress.trim().lowercase(),

            title = draft.title,
            description = draft.description
        )
    }

    fun resetWizard() {
        updateState {
            ClassifiedWizardState()
        }
    }
}
