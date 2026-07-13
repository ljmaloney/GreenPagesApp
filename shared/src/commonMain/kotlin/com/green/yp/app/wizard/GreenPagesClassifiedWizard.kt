package com.green.yp.app.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.green.yp.app.components.AlertBanner
import com.green.yp.app.components.AlertBannerItem
import com.green.yp.app.components.AlertType
import com.green.yp.app.components.EmailValidationComponent
import com.green.yp.app.components.GreenPagesTopBar
import com.green.yp.app.components.WizardProgressIndicator
import com.green.yp.app.components.WizardStep
import com.green.yp.app.media.ImagePicker
import com.green.yp.app.media.LocalImagePicker
import com.green.yp.app.payment.SquarePaymentProcessor
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.EmailContactViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.ui.theme.LightLightGold
import com.green.yp.app.wizard.components.AdDetails
import com.green.yp.app.wizard.components.AdLocation
import com.green.yp.app.wizard.components.ClassifiedAdSelector
import com.green.yp.app.wizard.components.ClassifiedPreview
import com.green.yp.app.wizard.components.ContactInformation
import com.green.yp.app.wizard.components.PaymentSuccess
import com.green.yp.app.wizard.components.UploadImages
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun GreenPagesClassifiedWizard(
    searchViewModel: SearchViewModel = koinViewModel(),
    classifiedReferenceViewModel: ClassifiedReferenceViewModel = koinViewModel(),
    referenceViewModel: ReferenceViewModel = koinViewModel(),
    classifiedViewModel: ClassifiedViewModel = koinViewModel(),
    emailContactViewModel: EmailContactViewModel = koinViewModel(),
    wizardViewModel: ClassifiedWizardViewModel = koinViewModel<ClassifiedWizardViewModel>(),
    paymentProcessor: SquarePaymentProcessor = koinInject(),
    imagePicker: ImagePicker? = null,
    onBackClick: () -> Unit = {},
    onNavigateHome: (initialTab: Int) -> Unit = {}
) {
    val log = Logger.withTag("green.yp.app.wizard.GreenPagesClassifiedWizard")
    val scope = rememberCoroutineScope()
    val state by wizardViewModel.state.collectAsState()
    val emailLoading by emailContactViewModel.isLoading.collectAsState()
    val emailError by emailContactViewModel.errorMessage.collectAsState()
    val emailValidated by emailContactViewModel.isValidated.collectAsState()
    
    val providedImagePicker = imagePicker ?: LocalImagePicker.current
    
    val scrollState = rememberScrollState()
    
    // Local state to store validation code for later use in payment
    var validationCode by remember { mutableStateOf("") }
    
    // Local state to hold the draft during the current step. 
    // It resets to the committed draft whenever the step changes.
    var workingDraft by remember(state.currentStep, state.draft) { mutableStateOf(state.draft) }

    val wizardSteps = remember(state.draft.adType) {
        val steps = mutableListOf(
            WizardStep("Package"),
            WizardStep("Details"),
            WizardStep("Location"),
            WizardStep("Contact"),
            WizardStep("Email Validation")
        )
        
        // Check if ad type has been selected and has images enabled
        val maxImages = state.draft.adType?.let { adTypeId ->
            classifiedReferenceViewModel.adTypes.value.find { it.adTypeId == adTypeId }?.features?.maxImages ?: 0
        } ?: 0
        
        if (maxImages > 0) {
            steps.add(WizardStep("Images"))
        }
        
        steps.add(WizardStep("Preview"))
        steps
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.White,
        topBar = {
            Column {
                GreenPagesTopBar(
                    onSearchClick = { onNavigateHome(1) },
                    onLogoClick = { onNavigateHome(0) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                WizardProgressIndicator(
                    steps = wizardSteps,
                    currentStep = wizardSteps.indexOfFirst { it.title.uppercase().replace(" ", "_") == state.currentStep.name }
                        .takeIf { it >= 0 } ?: 0
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        ,bottomBar = {
            val currentStepIndex = wizardSteps.indexOfFirst { it.title.uppercase().replace(" ", "_") == state.currentStep.name }
                .takeIf { it >= 0 } ?: 0
            if (currentStepIndex < wizardSteps.size - 1) {
                Surface(
                    color = LightLightGold,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClassifiedWizardBottomBar(
                        onBack = {
                            if (currentStepIndex == 0) {
                                onBackClick()
                            } else {
                                wizardViewModel.previousStep()
                            }
                        },
                        onNext = {
                            log.d("onNext called")
                        // If we are on Email Validation step, only allow Next if email is validated
                        if (state.currentStep == ClassifiedWizardStep.EMAIL_VALIDATION && !emailValidated) {
                             return@ClassifiedWizardBottomBar
                        }

                        // Trim trailing whitespace from all string fields in workingDraft before committing
                        val trimmedDraft = workingDraft.copy(
                                firstName = workingDraft.firstName.trimEnd(),
                                lastName = workingDraft.lastName.trimEnd(),
                                address = workingDraft.address.trimEnd(),
                                city = workingDraft.city.trimEnd(),
                                state = workingDraft.state.trimEnd(),
                                postalCode = workingDraft.postalCode.trimEnd(),
                                phoneNumber = workingDraft.phoneNumber.trimEnd(),
                                emailAddress = workingDraft.emailAddress.trimEnd(),
                                title = workingDraft.title.trimEnd(),
                                description = workingDraft.description.trimEnd(),
                                pricePerUnitType = workingDraft.pricePerUnitType?.trimEnd()
                            )
                            
                            // Commit the trimmed draft to the ViewModel before moving to the next step
                            wizardViewModel.updateDraft { trimmedDraft }
                            scope.launch {
                                log.d("onNext launch called")
                                wizardViewModel.nextStep()
                            }
                        },
                        onPreview = { },
                        currentStep = currentStepIndex,
                        totalSteps = wizardSteps.size,
                        isLoading = state.loading || emailLoading,
                        isNextEnabled = if (state.currentStep == ClassifiedWizardStep.EMAIL_VALIDATION) emailValidated else true,
                        isBackEnabled = true,
                        viewModel = wizardViewModel
                    )
                }
            }
        }
    ) { paddingValues ->
        Column( modifier =  Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
                when (state.currentStep) {
                    ClassifiedWizardStep.PACKAGE -> {
                        ClassifiedAdSelector(
                            viewModel = classifiedReferenceViewModel,
                            selectedAdType = workingDraft.adType,
                            onAdTypeSelected = { adType ->
                                workingDraft = workingDraft.copy(adType = adType.adTypeId)
                            }
                        )
                    }

                    ClassifiedWizardStep.DETAILS -> {
                        AdDetails(
                            viewModel = classifiedReferenceViewModel,
                            draft = workingDraft,
                            onDraftChange = { workingDraft = it }
                        )
                    }

                    ClassifiedWizardStep.LOCATION -> {
                        AdLocation(
                            draft = workingDraft,
                            onDraftChange = { workingDraft = it }
                        )
                    }

                    ClassifiedWizardStep.CONTACT -> {
                        ContactInformation(
                            draft = workingDraft,
                            onDraftChange = { workingDraft = it }
                        )
                    }

                    ClassifiedWizardStep.EMAIL_VALIDATION -> {
                        var showSuccessMessage by remember(emailValidated) { mutableStateOf(emailValidated) }
                        
                        // Reset validation state when entering this step for the first time
                        LaunchedEffect(Unit) {
                            if (!emailValidated) {
                                emailContactViewModel.resetValidationState()
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            emailError?.let { error ->
                                AlertBanner(
                                    alerts = listOf(
                                        AlertBannerItem(
                                            id = "email-validation-error",
                                            title = "Validation Error",
                                            message = error,
                                            type = AlertType.ERROR
                                        )
                                    ),
                                    onDismiss = { emailContactViewModel.clearError() }
                                )
                            }

                            if (showSuccessMessage) {
                                AlertBanner(
                                    alerts = listOf(
                                        AlertBannerItem(
                                            id = "email-validation-success",
                                            title = "Email Verified",
                                            message = "Thanks for taking the time to verify your email address",
                                            type = AlertType.INFO
                                        )
                                    ),
                                    onDismiss = { showSuccessMessage = false }
                                )
                            }

                            EmailValidationComponent(
                                isLoading = emailLoading,
                                onValidate = { code ->
                                    validationCode = code
                                    val listingId = state.listingId?.toString() ?: ""
                                    val email = state.draft.emailAddress
                                    emailContactViewModel.validateEmail(
                                        externRef = listingId,
                                        emailAddress = email,
                                        token = code
                                    )
                                }
                            )
                        }
                    }

                    ClassifiedWizardStep.IMAGES -> {
                        state.listingId?.let { listingId ->
                            providedImagePicker?.let { picker ->
                                UploadImages(
                                    classifiedId = listingId,
                                    maxImages = state.draft.adType?.let { adTypeId ->
                                        classifiedReferenceViewModel.adTypes.value.find { it.adTypeId == adTypeId }?.features?.maxImages
                                            ?: 0
                                    } ?: 0,
                                    viewModel = classifiedViewModel,
                                    imagePicker = picker
                                )
                            } ?: Text(
                                "Image selection is not available on this platform",
                                modifier = Modifier.padding(16.dp)
                            )
                        } ?: Text(
                            "Please complete earlier steps first",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    ClassifiedWizardStep.PREVIEW -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                state.listingId?.let { listingId ->
                                    ClassifiedPreview(
                                        classifiedId = listingId,
                                        viewModel = classifiedViewModel,
                                        referenceViewModel = classifiedReferenceViewModel
                                    )
                                } ?: Text("Ad data not found", modifier = Modifier.padding(16.dp))
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val adType = classifiedReferenceViewModel.adTypes.value.find { it.adTypeId == state.draft.adType }
                                        val price = (adType?.monthlyPrice ?: 0.0) * 100 // Convert to cents
                                        
                                        wizardViewModel.startPaymentFlow(
                                            paymentProcessor = paymentProcessor,
                                            amount = price.toLong(),
                                            currency = "USD",
                                            emailValidationToken = validationCode
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.loading,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                                ) {
                                    Text("Place Ad", color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = { wizardViewModel.previousStep() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.loading,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                        brush = SolidColor(DarkGreen)
                                    )
                                ) {
                                    Text("<< Back")
                                }
                            }
                        }
                    }

                    ClassifiedWizardStep.PAYMENT -> {
                        state.paymentResponse?.let { response ->
                            PaymentSuccess(
                                response = response,
                                onFinish = { onNavigateHome(0) }
                            )
                        } ?: Text("Payment processing...", modifier = Modifier.padding(16.dp))
                    }
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
fun GreenPagesClassifiedWizardPreview() {
    Text("Simple Preview Test")
}
