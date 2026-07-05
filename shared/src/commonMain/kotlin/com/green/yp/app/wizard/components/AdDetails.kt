package com.green.yp.app.wizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.components.ChipItem
import com.green.yp.app.components.ChipSelector
import com.green.yp.app.components.ThreeItemSpinner
import com.green.yp.app.enum.PricePerEnum
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import com.green.yp.app.shared.repository.ClassifiedReferenceRepository
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.wizard.ClassifiedDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun AdDetails(
    viewModel: ClassifiedReferenceViewModel,
    draft: ClassifiedDraft,
    onDraftChange: (ClassifiedDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    
    val initialPrice = draft.price
    var priceInput by remember(initialPrice) { 
        mutableStateOf(initialPrice?.let { 
            if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() 
        } ?: "") 
    }
    
    val chipItems = remember(categories) {
        categories.map { ChipItem(it.categoryId, it.name) }
    }
    
    val selectedPricePerIndex = if (!draft.pricePerUnitType.isNullOrBlank()) {
        PricePerEnum.entries.indexOfFirst { it.displayName == draft.pricePerUnitType }
            .takeIf { it >= 0 } ?: 0
    } else 0

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "2. Enter Your Ad Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ChipSelector(
            title = "Category*",
            items = chipItems,
            selectedId = draft.categoryId,
            onItemSelected = { 
                onDraftChange(draft.copy(categoryId = it.id))
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Price (USD)*",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = priceInput,
                onValueChange = { input ->
                    // Allow only numbers and a single decimal point with up to 2 decimal places
                    if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                        priceInput = input
                        val priceValue = if (input.isEmpty() || input == ".") null else input.toDoubleOrNull()
                        onDraftChange(draft.copy(price = priceValue))
                    }
                },
                placeholder = { Text("0.00") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Price Per (Optional)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ThreeItemSpinner(
                    items = PricePerEnum.entries,
                    selectedIndex = selectedPricePerIndex,
                    itemLabel = { it.displayName },
                    onSelected = { 
                        onDraftChange(draft.copy(pricePerUnitType = PricePerEnum.entries[it].displayName))
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Classified Ad Title",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = draft.title,
                onValueChange = { input ->
                    // Allow only alphabetical and numerical characters
                    if (input.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                        onDraftChange(draft.copy(title = input))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Item Description",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = draft.description,
                onValueChange = { 
                    onDraftChange(draft.copy(description = it))
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                minLines = 3
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
fun AdDetailsPreview() {
    val mockRepo = object : ClassifiedReferenceRepository {
        override val categories = MutableStateFlow(
            listOf(
                ClassifiedCategory(Uuid.random(), true, "Hay", "hay"),
                ClassifiedCategory(Uuid.random(), true, "Livestock", "livestock"),
                ClassifiedCategory(Uuid.random(), true, "Equipment", "equipment"),
                ClassifiedCategory(Uuid.random(), true, "Seeds", "seeds")
            )
        )
        override val adTypes = MutableStateFlow(emptyList<ClassifiedAdType>())
        override val errorMessage = MutableStateFlow<String?>(null)
        override suspend fun getCategories() = Result.success(categories.value)
        override suspend fun getClassifiedAdTypes() = Result.success(emptyList<ClassifiedAdType>())
    }
    
    val viewModel = ClassifiedReferenceViewModel(mockRepo)
    
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AdDetails(
                viewModel = viewModel,
                draft = ClassifiedDraft(),
                onDraftChange = {}
            )
        }
    }
}
