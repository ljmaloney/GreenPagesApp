package com.green.yp.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.green.yp.app.components.ClassifiedWizardBottomBar
import com.green.yp.app.components.GreenPagesTopBar
import com.green.yp.app.components.WizardProgressIndicator
import com.green.yp.app.components.WizardStep
import com.green.yp.app.shared.dto.PageableResponse
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import com.green.yp.app.shared.dto.reference.LineOfBusiness
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.repository.ClassifiedReferenceRepository
import com.green.yp.app.shared.repository.ReferenceRepository
import com.green.yp.app.shared.repository.SearchRepository
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GreenPagesWizard(
    searchViewModel: SearchViewModel = koinViewModel(),
    classifiedReferenceViewModel: ClassifiedReferenceViewModel = koinViewModel(),
    referenceViewModel: ReferenceViewModel = koinViewModel()
) {
    var currentStep by remember { mutableStateOf(0) }
    val wizardSteps = remember {
        listOf(
            WizardStep("Category"),
            WizardStep("Ad Type"),
            WizardStep("Ad Details"),
            WizardStep("Review")
        )
    }

    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    GreenPagesTopBar(
                        onSearchClick = { /* Not used in wizard */ }
                    )
                    WizardProgressIndicator(
                        steps = wizardSteps,
                        currentStep = currentStep
                    )
                }
            },
            bottomBar = {
                ClassifiedWizardBottomBar(
                    onBack = { if (currentStep > 0) currentStep-- },
                    onNext = { if (currentStep < wizardSteps.size - 1) currentStep++ },
                    onPreview = { /* TODO: Implement preview logic */ },
                    currentStep = currentStep,
                    totalSteps = wizardSteps.size
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                when (currentStep) {
                    0 -> Text("Step 1: Select Category", modifier = Modifier.padding(16.dp))
                    1 -> Text("Step 2: Select Ad Type", modifier = Modifier.padding(16.dp))
                    2 -> Text("Step 3: Enter Ad Details", modifier = Modifier.padding(16.dp))
                    3 -> Text("Step 4: Review and Submit", modifier = Modifier.padding(16.dp))
                    else -> Text("Unknown Step")
                }
            }
        }
    }
}

@Preview
@Composable
fun GreenPagesWizardPreview() {
    val mockSearchRepo = object : SearchRepository {
        override val searchResults = MutableStateFlow(
            PageableResponse<SearchResponseDTO>(emptyList(), 0, 0, 0)
        )
        override val errorMessage = MutableStateFlow<String?>(null)
        override suspend fun search(zipCode: String?, keywords: String?, categoryRefId: String?, distance: Int?, page: Int?, limit: Int?) = Result.success(searchResults.value!!)
        override suspend fun search(latitude: Double?, longitude: Double?, keywords: String?, categoryRefId: String?, distance: Int?, page: Int?, limit: Int?) = Result.success(searchResults.value!!)
    }

    val mockClassifiedRepo = object : ClassifiedReferenceRepository {
        override val categories = MutableStateFlow(emptyList<ClassifiedCategory>())
        override val adTypes = MutableStateFlow(emptyList<ClassifiedAdType>())
        override val errorMessage = MutableStateFlow<String?>(null)
        override suspend fun getCategories() = Result.success(emptyList<ClassifiedCategory>())
        override suspend fun getClassifiedAdTypes() = Result.success(emptyList<ClassifiedAdType>())
    }

    val mockReferenceRepo = object : ReferenceRepository {
        override val linesOfBusiness = MutableStateFlow(emptyList<LineOfBusiness>())
        override val errorMessage = MutableStateFlow<String?>(null)
        override suspend fun getLinesOfBusiness() = Result.success(emptyList<LineOfBusiness>())
    }

    val searchVM = SearchViewModel(mockSearchRepo)
    val classifiedVM = ClassifiedReferenceViewModel(mockClassifiedRepo)
    val referenceVM = ReferenceViewModel(mockReferenceRepo)

    GreenPagesWizard(
        searchViewModel = searchVM,
        classifiedReferenceViewModel = classifiedVM,
        referenceViewModel = referenceVM
    )
}
