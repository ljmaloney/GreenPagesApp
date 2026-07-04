package com.green.yp.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.components.ClassifiedWizardBottomBar
import com.green.yp.app.components.EmailValidationComponent
import com.green.yp.app.components.GreenPagesTopBar
import com.green.yp.app.components.WizardProgressIndicator
import com.green.yp.app.components.WizardStep
import com.green.yp.app.components.classified.*
import com.green.yp.app.media.ImagePicker
import com.green.yp.app.media.ImageResult
import com.green.yp.app.shared.dto.PageableResponse
import com.green.yp.app.shared.dto.classified.*
import com.green.yp.app.shared.dto.reference.LineOfBusiness
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.repository.ClassifiedReferenceRepository
import com.green.yp.app.shared.repository.ClassifiedRepository
import com.green.yp.app.shared.repository.ReferenceRepository
import com.green.yp.app.shared.repository.SearchRepository
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import com.green.yp.app.ui.theme.DarkGreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun GreenPagesClassifiedWizard(
    searchViewModel: SearchViewModel = koinViewModel(),
    classifiedReferenceViewModel: ClassifiedReferenceViewModel = koinViewModel(),
    referenceViewModel: ReferenceViewModel = koinViewModel(),
    classifiedViewModel: ClassifiedViewModel = koinViewModel(),
    imagePicker: ImagePicker // This should be provided by koin or composition local in a real app
) {
    var currentStepIndex by remember { mutableStateOf(0) }
    val createdAd by classifiedViewModel.createdAd.collectAsState()
    
    var selectedAdType by remember { mutableStateOf<ClassifiedAdType?>(null) }
    var emailValidationCode by remember { mutableStateOf("") }
    
    // Dynamic steps based on selected ad type
    val wizardSteps = remember(selectedAdType) {
        val steps = mutableListOf(
            WizardStep("Ad Package"),
            WizardStep("Ad Details"),
            WizardStep("Ad Location"),
            WizardStep("Contact Info"),
            WizardStep("Validate Email")
        )
        
        if ((selectedAdType?.features?.maxImages ?: 0) > 0) {
            steps.add(WizardStep("Upload Images"))
        }
        
        steps.add(WizardStep("Preview"))
        steps
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                GreenPagesTopBar(
                    onSearchClick = { /* Not used in wizard */ }
                )
                WizardProgressIndicator(
                    steps = wizardSteps,
                    currentStep = currentStepIndex
                )
            }
        },
        bottomBar = {
            if (currentStepIndex < wizardSteps.size - 1) {
                ClassifiedWizardBottomBar(
                    onBack = { if (currentStepIndex > 0) currentStepIndex-- },
                    onNext = { 
                        if (currentStepIndex < wizardSteps.size - 1) currentStepIndex++ 
                    },
                    onPreview = { currentStepIndex = wizardSteps.size - 1 },
                    currentStep = currentStepIndex,
                    totalSteps = wizardSteps.size
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            val currentStepTitle = wizardSteps[currentStepIndex].title
            
            when (currentStepTitle) {
                "Ad Package" -> {
                    ClassifiedAdTypeSelector(
                        viewModel = classifiedReferenceViewModel,
                        onAdTypeSelected = { 
                            selectedAdType = it
                        }
                    )
                }
                "Ad Details" -> {
                    AdDetails(
                        viewModel = classifiedReferenceViewModel
                    )
                }
                "Ad Location" -> {
                    AdLocation()
                }
                "Contact Info" -> {
                    ContactInformation()
                }
                "Validate Email" -> {
                    EmailValidationComponent(
                        onValidate = { code ->
                            emailValidationCode = code
                        }
                    )
                }
                "Upload Images" -> {
                    createdAd?.let { ad ->
                        UploadImages(
                            classifiedId = ad.classifiedId,
                            maxImages = selectedAdType?.features?.maxImages ?: 0,
                            viewModel = classifiedViewModel,
                            imagePicker = imagePicker
                        )
                    } ?: Text("Please complete Ad Details first", modifier = Modifier.padding(16.dp))
                }
                "Preview" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            createdAd?.let { ad ->
                                ClassifiedPreview(
                                    classifiedId = ad.classifiedId,
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
