package com.green.yp.app

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import com.green.yp.app.components.GreenPagesBottomBar
import com.green.yp.app.components.GreenPagesTopBar
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.screens.ExploreMarketResultsScreen
import com.green.yp.app.screens.SearchScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GreenPagesMainScreen(
    searchViewModel: SearchViewModel = koinViewModel(),
    classifiedViewModel: ClassifiedViewModel = koinViewModel(),
    referenceViewModel: ReferenceViewModel = koinViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }

    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                GreenPagesTopBar(
                    onSearchClick = { selectedTab = 1 }
                )
            },
            bottomBar = {
                GreenPagesBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { paddingValues ->
            when (selectedTab) {
                0 -> ExploreMarketResultsScreen(paddingValues, viewModel = searchViewModel)
                1 -> SearchScreen(
                    classifiedView = classifiedViewModel,
                    referenceViewModel = referenceViewModel,
                    paddingValues = paddingValues
                )
                else -> {
                    // TODO: Other screens
                    Column(modifier = Modifier.padding(paddingValues)) {
                        Text("Tab $selectedTab Content")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GreenPagesMainScreenPreview() {
    val mockSearchRepo = object : com.green.yp.app.shared.repository.SearchRepository {
        override val searchResults = kotlinx.coroutines.flow.MutableStateFlow(
            com.green.yp.app.shared.dto.PageableResponse<com.green.yp.app.shared.dto.search.SearchResponseDTO>(emptyList(), 0, 0, 0)
        )
        override val errorMessage = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
        override suspend fun search(zipCode: String?, keywords: String?, categoryRefId: String?, distance: Int?, page: Int?, limit: Int?) = Result.success(searchResults.value)
        override suspend fun search(latitude: Double?, longitude: Double?, keywords: String?, categoryRefId: String?, distance: Int?, page: Int?, limit: Int?) = Result.success(searchResults.value)
    }

    val mockClassifiedRepo = object : com.green.yp.app.shared.repository.ClassifiedRepository {
        override val categories = kotlinx.coroutines.flow.MutableStateFlow(emptyList<com.green.yp.app.shared.dto.classified.ClassifiedCategory>())
        override val errorMessage = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
        override suspend fun getCategories() = Result.success(emptyList<com.green.yp.app.shared.dto.classified.ClassifiedCategory>())
    }

    val mockReferenceRepo = object : com.green.yp.app.shared.repository.ReferenceRepository {
        override val linesOfBusiness = kotlinx.coroutines.flow.MutableStateFlow(emptyList<com.green.yp.app.shared.dto.reference.LineOfBusiness>())
        override val errorMessage = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
        override suspend fun getLinesOfBusiness() = Result.success(emptyList<com.green.yp.app.shared.dto.reference.LineOfBusiness>())
    }

    val searchVM = SearchViewModel(mockSearchRepo)
    val classifiedVM = ClassifiedViewModel(mockClassifiedRepo)
    val referenceVM = ReferenceViewModel(mockReferenceRepo)

    GreenPagesMainScreen(
        searchViewModel = searchVM,
        classifiedViewModel = classifiedVM,
        referenceViewModel = referenceVM
    )
}
