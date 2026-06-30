package com.green.yp.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.getLocationManager
import com.green.yp.app.components.SearchResultView
import com.green.yp.app.shared.dto.PageableResponse
import com.green.yp.app.shared.dto.search.SearchRecordType
import com.green.yp.app.shared.dto.search.SearchRequestParams
import com.green.yp.app.shared.dto.search.SearchResponseDTO
import com.green.yp.app.shared.repository.SearchRepository
import com.green.yp.app.shared.viewmodel.SearchViewModel
import com.green.yp.app.ui.theme.DarkGreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExploreMarketResultsScreen(
    paddingValues: PaddingValues,
    params: SearchRequestParams? = null,
    viewModel: SearchViewModel = koinViewModel()
) {
    val results by viewModel.searchResults.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    val locationManager = remember { getLocationManager() }
    val userLocation by locationManager.locationUpdates.collectAsState()

    val listState = rememberLazyListState()
    var initialSearchPerformed by remember { mutableStateOf(false) }

    // Trigger initial search when screen is displayed
    LaunchedEffect(params, userLocation) {
        if (params != null) {
            viewModel.search(
                zipCode = params.zipCode,
                latitude = params.latitude,
                longitude = params.longitude,
                keywords = params.keywords,
                categoryRefId = params.categoryRefId,
                distance = params.distance
            )
        } else if (!initialSearchPerformed) {
            // If params not specified, use current location for the FIRST time
            userLocation?.let { location ->
                initialSearchPerformed = true
                viewModel.search(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    keywords = null,
                    categoryRefId = null,
                    distance = 25
                )
            }
        }
    }

    // Pagination logic: detect when near the end of the list
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            // Load more when user is 5 items away from the end
            lastVisibleItemIndex >= totalItemsCount - 5 && totalItemsCount > 0
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value }
            .collect { shouldLoad ->
                if (shouldLoad && !isRefreshing && !isLoadingMore) {
                    viewModel.loadMore()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (errorMessage != null && results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        } else if (isRefreshing && results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkGreen)
            }
        } else {
            if (results.isEmpty() && !isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found.")
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(items = results) { result ->
                        SearchResultView(
                            result = result,
                            onClick = { /* Handle item click */ }
                        )
                    }
                    
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = DarkGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExploreMarketResultsScreenPreview() {
    val sampleResults = listOf(
        SearchResponseDTO(
            externId = "1", producerId = "p1", locationId = "l1", categoryRef = "cat1",
            categoryName = "Gardening", recordType = SearchRecordType.GREEN_PRO, active = true,
            title = "Eco Landscaping", businessName = "Green Thumb",
            city = "Portland", state = "OR", postalCode = "97201", addressLine1 = "123 Eco Way",
            distance = 2.5, description = "Eco-friendly landscaping services.",
            longitude = 0.0, latitude = 0.0
        ),
        SearchResponseDTO(
            externId = "2", producerId = "p2", locationId = "l2", categoryRef = "cat2",
            categoryName = "Food", recordType = SearchRecordType.GREEN_PRO_PRODUCT, active = true,
            title = "Organic Apples", businessName = "Happy Farm",
            city = "Portland", state = "OR", postalCode = "97202", addressLine1 = "456 Farm Lane",
            distance = 5.0, description = "Fresh organic apples from the farm.",
            longitude = 0.0, latitude = 0.0
        )
    )

    val mockRepository = object : SearchRepository {
        override val searchResults = MutableStateFlow(
            PageableResponse(
                pageableResults = sampleResults,
                totalCount = 2,
                currentPage = 0,
                totalPages = 1
            )
        )
        override val errorMessage = MutableStateFlow<String?>(null)

        override suspend fun search(
            zipCode: String?, keywords: String?, categoryRefId: String?,
            distance: Int?, page: Int?, limit: Int?
        ) = Result.success(searchResults.value)

        override suspend fun search(
            latitude: Double?, longitude: Double?, keywords: String?,
            categoryRefId: String?, distance: Int?, page: Int?, limit: Int?
        ) = Result.success(searchResults.value)
    }

    val mockViewModel = SearchViewModel(mockRepository)

    MaterialTheme {
        ExploreMarketResultsScreen(
            paddingValues = PaddingValues(16.dp),
            viewModel = mockViewModel
        )
    }
}
