package com.green.yp.app.wizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.dto.classified.ClassifiedAdFeatures
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.ui.theme.DarkGreen
import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import androidx.compose.ui.tooling.preview.Preview

/**
 * A selector component that displays a list of [ClassifiedAdType] in a horizontally scrollable pager.
 * Includes indicator dots at the bottom.
 */
@OptIn(ExperimentalUuidApi::class)
@Composable
fun ClassifiedAdTypeSelector(
    viewModel: ClassifiedReferenceViewModel,
    modifier: Modifier = Modifier,
    onAdTypeSelected: (ClassifiedAdType) -> Unit = {}
) {
    val adTypes by viewModel.adTypes.collectAsState()

    if (adTypes.isNotEmpty()) {
        ClassifiedAdTypeSelectorContent(
            adTypes = adTypes,
            modifier = modifier,
            onAdTypeSelected = onAdTypeSelected
        )
    }
}

/**
 * Stateless content for the [ClassifiedAdTypeSelector].
 */
@OptIn(ExperimentalUuidApi::class)
@Composable
fun ClassifiedAdTypeSelectorContent(
    adTypes: List<ClassifiedAdType>,
    modifier: Modifier = Modifier,
    onAdTypeSelected: (ClassifiedAdType) -> Unit = {}
) {
    val initialPage = remember(adTypes) {
        val index = adTypes.indexOfFirst { it.defaultPackage }
        if (index >= 0) index else 0
    }
    
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { adTypes.size }
    )
    
    var selectedId by remember { 
        mutableStateOf(adTypes.getOrNull(initialPage)?.adTypeId) 
    }

    // Ensure onAdTypeSelected is called for the initial selection if it's the first time
    LaunchedEffect(selectedId) {
        if (selectedId != null) {
            adTypes.find { it.adTypeId == selectedId }?.let {
                onAdTypeSelected(it)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 0.dp),
            pageSpacing = 0.dp
        ) { page ->
            val adType = adTypes[page]
            ClassifiedAdTypeCard(
                adType = adType,
                isSelected = adType.adTypeId == selectedId,
                modifier = Modifier.height(450.dp), // Fixed height for all cards
                onClick = {
                    selectedId = adType.adTypeId
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Indicator Dots
        Row(
            Modifier
                .height(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(adTypes.size) { iteration ->
                val isCurrentPage = pagerState.currentPage == iteration
                val color = if (isCurrentPage) DarkGreen else Color.LightGray
                val width = if (isCurrentPage) 24.dp else 8.dp
                
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(width = width, height = 8.dp)
                        .animateContentSize()
                )
            }
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalUuidApi::class)
fun ClassifiedAdTypeSelectorPreview() {
    val sampleFeatures = ClassifiedAdFeatures(
        features = listOf("Feature 1", "Feature 2"),
        maxImages = 5,
        protectContact = true
    )
    val sampleAdTypes = listOf(
        ClassifiedAdType(
            adTypeId = Uuid.random(),
            createDate = "",
            active = true,
            defaultPackage = false,
            adTypeName = "Basic",
            monthlyPrice = 9.99,
            threeMonthPrice = 25.0,
            features = sampleFeatures
        ),
        ClassifiedAdType(
            adTypeId = Uuid.random(),
            createDate = "",
            active = true,
            defaultPackage = true,
            adTypeName = "Professional",
            monthlyPrice = 19.99,
            threeMonthPrice = 50.0,
            features = sampleFeatures
        )
    )

    MaterialTheme {
        ClassifiedAdTypeSelectorContent(adTypes = sampleAdTypes)
    }
}
