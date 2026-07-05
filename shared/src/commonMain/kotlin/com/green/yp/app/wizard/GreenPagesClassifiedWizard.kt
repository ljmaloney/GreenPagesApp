package com.green.yp.app.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.green.yp.app.components.EmailValidationComponent
import com.green.yp.app.components.GreenPagesTopBar
import com.green.yp.app.components.WizardProgressIndicator
import com.green.yp.app.components.WizardStep
import com.green.yp.app.media.ImagePicker
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.wizard.components.AdDetails
import com.green.yp.app.wizard.components.AdLocation
import com.green.yp.app.wizard.components.ClassifiedAdSelector
import com.green.yp.app.wizard.components.ClassifiedAdTypeSelector
import com.green.yp.app.wizard.components.ClassifiedPreview
import com.green.yp.app.wizard.components.ContactInformation
import com.green.yp.app.wizard.components.UploadImages
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun GreenPagesClassifiedWizard(
    searchViewModel: SearchViewModel = koinViewModel(),
    classifiedReferenceViewModel: ClassifiedReferenceViewModel = koinViewModel(),
    referenceViewModel: ReferenceViewModel = koinViewModel(),
    classifiedViewModel: ClassifiedViewModel = koinViewModel(),
    wizardViewModel: ClassifiedWizardViewModel = koinViewModel<ClassifiedWizardViewModel>(),
    imagePicker: ImagePicker? = null, // This should be provided by koin or composition local in a real app
    onBackClick: () -> Unit = {},
    onNavigateHome: (initialTab: Int) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val state by wizardViewModel.state.collectAsState()
    
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
        modifier = Modifier.fillMaxSize(),
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
        },
        bottomBar = {
            val currentStepIndex = wizardSteps.indexOfFirst { it.title.uppercase().replace(" ", "_") == state.currentStep.name }
                .takeIf { it >= 0 } ?: 0
            if (currentStepIndex < wizardSteps.size - 1) {
                ClassifiedWizardBottomBar(
                    onBack = { 
                        if (currentStepIndex == 0) {
                            onBackClick()
                        } else {
                            wizardViewModel.previousStep()
                        }
                    },
                    onNext = {
                        // Commit the working draft to the ViewModel before moving to the next step
                        wizardViewModel.updateDraft { workingDraft }
                        scope.launch {
                            wizardViewModel.nextStep()
                        }
                    },
                    onPreview = { },
                    currentStep = currentStepIndex,
                    totalSteps = wizardSteps.size,
                    isLoading = state.loading,
                    viewModel = wizardViewModel
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            
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
                    EmailValidationComponent(
                        onValidate = { code ->
                            // Handle email validation
                            // This would typically call wizardViewModel to validate the email
                        }
                    )
                }
                ClassifiedWizardStep.IMAGES -> {
                    state.listingId?.let { listingId ->
                       imagePicker?.let { picker ->
                           UploadImages(
                               classifiedId = listingId,
                               maxImages = state.draft.adType?.let { adTypeId ->
                                   classifiedReferenceViewModel.adTypes.value.find { it.adTypeId == adTypeId }?.features?.maxImages ?: 0
                               } ?: 0,
                               viewModel = classifiedViewModel,
                               imagePicker = picker
                           )
                       } ?: Text("Please complete earlier steps first", modifier = Modifier.padding(16.dp))
                    }
                }
                ClassifiedWizardStep.PREVIEW -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            state.listingId?.let { listingId ->
                                ClassifiedPreview(
                                    classifiedId = listingId,
                                    viewModel = classifiedViewModel
                                )
                            } ?: Text("Ad data not found", modifier = Modifier.padding(16.dp))
                        }
                        
                        Button(
                            onClick = { /* TODO: Implement Place Ad logic */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                        ) {
                            Text("Place Ad", color = Color.White)
                        }
                    }
                }
                ClassifiedWizardStep.PAYMENT -> {
                    Text("Payment step", modifier = Modifier.padding(16.dp))
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
