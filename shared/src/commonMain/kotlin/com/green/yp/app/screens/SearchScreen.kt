package com.green.yp.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.UserLocation
import com.green.yp.app.getLocationManager
import com.green.yp.app.shared.dto.classified.ClassifiedCategory
import com.green.yp.app.shared.dto.reference.LineOfBusiness
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.ui.theme.DarkGold
import com.green.yp.app.ui.theme.DarkGreen
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.round
import kotlin.uuid.Uuid

data class SearchCategory(val id: Uuid, val name: String)

@Composable
fun SearchScreen(classifiedView: ClassifiedViewModel = koinViewModel(),
                 referenceViewModel: ReferenceViewModel = koinViewModel(),
                 paddingValues: PaddingValues = PaddingValues(16.dp)
) {
    val locationManager = remember { getLocationManager() }
    val userLocation by locationManager.locationUpdates.collectAsState()
    val classifiedCategories by classifiedView.categories.collectAsState()
    val linesOfBusiness by referenceViewModel.linesOfBusiness.collectAsState()

    var stableLocation by remember { mutableStateOf<UserLocation?>(null) }

    LaunchedEffect(Unit) {
        classifiedView.fetchCategories()
        referenceViewModel.fetchLinesOfBusiness()
    }

    LaunchedEffect(userLocation) {
        if (stableLocation == null && userLocation != null) {
            stableLocation = UserLocation(
                latitude = round(userLocation!!.latitude * 10000.0) / 10000.0,
                longitude = round(userLocation!!.longitude * 10000.0) / 10000.0
            )
        }
    }

    val combinedCategories by remember(classifiedCategories, linesOfBusiness) {
        derivedStateOf {
            (classifiedCategories.map { category: ClassifiedCategory -> 
                SearchCategory(category.categoryId, category.name) 
            } + linesOfBusiness.map { lob: LineOfBusiness -> 
                SearchCategory(lob.lineOfBusinessId, lob.lineOfBusinessName) 
            })
                .sortedBy { it.name }
        }
    }
    
    SearchScreenContent(
        paddingValues = paddingValues,
        userLocation = stableLocation,
        categories = combinedCategories
    )
}

@Composable
fun SearchScreenContent(
    paddingValues: PaddingValues,
    userLocation: UserLocation?,
    categories: List<SearchCategory> = emptyList()
) {
    var keywords by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var selectedDistance by remember { mutableStateOf(10) }
    var distanceExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<SearchCategory?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val distanceOptions = listOf(
        10 to "10 miles",
        25 to "25 miles",
        50 to "50 miles",
        100 to "100 miles"
    )

    val isZipCodeRequired = userLocation == null
    val isFormValid = selectedDistance > 0 && (zipCode.isNotEmpty() || userLocation != null)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonColor = if (isPressed) DarkGold else DarkGreen

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Filter Providers",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp),
            color = DarkGreen
        )

        // Zip Code TextField
        OutlinedTextField(
            value = zipCode,
            onValueChange = { zipCode = it },
            label = {
                Text(
                    if (isZipCodeRequired) "Zip Code (Required)" else "Zip Code"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isZipCodeRequired && zipCode.isEmpty(),
            supportingText = {
                if (isZipCodeRequired && zipCode.isEmpty()) {
                    Text(
                        text = "GPS location not available. Zip code is required.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else if (userLocation != null) {
                    Text(
                        text = "Your location: ${userLocation.latitude}, ${userLocation.longitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,
                focusedLabelColor = DarkGold,
                unfocusedLabelColor = DarkGreen,
                errorLabelColor = Color.Red,
                focusedBorderColor = DarkGold,
                unfocusedBorderColor = DarkGreen,
                errorBorderColor = Color.Red,
                cursorColor = DarkGold,
                errorCursorColor = Color.Red
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Keywords TextField
        OutlinedTextField(
            value = keywords,
            onValueChange = { keywords = it },
            label = { Text("Keywords (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,
                focusedLabelColor = DarkGold,
                unfocusedLabelColor = DarkGreen,
                errorLabelColor = Color.Red,
                focusedBorderColor = DarkGold,
                unfocusedBorderColor = DarkGreen,
                errorBorderColor = Color.Red,
                cursorColor = DarkGold,
                errorCursorColor = Color.Red
            )
        )
        Spacer(modifier = Modifier.height(16.dp))



        // Distance Dropdown
        DistanceDropdown(
            selectedDistance = selectedDistance,
            onDistanceSelected = { selectedDistance = it },
            distanceOptions = distanceOptions,
            isExpanded = distanceExpanded,
            onExpandedChange = { distanceExpanded = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Category Dropdown
        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            categories = categories,
            isExpanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Search Button
        Button(
            onClick = { /* Handle search */ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            enabled = isFormValid,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.White
            ),
            interactionSource = interactionSource
        ) {
            Text("Search")
        }
    }
}

@Composable
fun CategoryDropdown(
    selectedCategory: SearchCategory?,
    onCategorySelected: (SearchCategory) -> Unit,
    categories: List<SearchCategory>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val width = maxWidth
        OutlinedTextField(
            value = selectedCategory?.name ?: "Select category",
            onValueChange = {},
            label = { Text("Category (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Text(if (isExpanded) "▲" else "▼", modifier = Modifier.padding(end = 12.dp), color = DarkGreen)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,
                focusedLabelColor = DarkGold,
                unfocusedLabelColor = DarkGreen,
                errorLabelColor = Color.Red,
                focusedBorderColor = DarkGold,
                unfocusedBorderColor = DarkGreen,
                errorBorderColor = Color.Red,
                cursorColor = DarkGold,
                errorCursorColor = Color.Red,
                disabledContainerColor = Color.White,
                disabledTextColor = Color.Black,
                disabledLabelColor = DarkGreen,
                disabledBorderColor = DarkGreen
            )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    onClick = { onExpandedChange(!isExpanded) }
                )
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .width(width)
                .background(Color.White)
                .border(1.dp, DarkGreen, MaterialTheme.shapes.extraSmall)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory?.id == category.id
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category.name,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    },
                    onClick = {
                        onCategorySelected(category)
                        onExpandedChange(false)
                    },
                    modifier = Modifier.background(if (isSelected) DarkGold else Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun DistanceDropdown(
    selectedDistance: Int,
    onDistanceSelected: (Int) -> Unit,
    distanceOptions: List<Pair<Int, String>>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val width = maxWidth
        OutlinedTextField(
            value = distanceOptions.find { it.first == selectedDistance }?.second ?: "Select distance",
            onValueChange = {},
            label = { Text("Distance (Required)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Text(if (isExpanded) "▲" else "▼", modifier = Modifier.padding(end = 12.dp), color = DarkGreen)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,
                focusedLabelColor = DarkGold,
                unfocusedLabelColor = DarkGreen,
                errorLabelColor = Color.Red,
                focusedBorderColor = DarkGold,
                unfocusedBorderColor = DarkGreen,
                errorBorderColor = Color.Red,
                cursorColor = DarkGold,
                errorCursorColor = Color.Red,
                disabledContainerColor = Color.White,
                disabledTextColor = Color.Black,
                disabledLabelColor = DarkGreen,
                disabledBorderColor = DarkGreen
            )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    onClick = { onExpandedChange(!isExpanded) }
                )
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .width(width)
                .background(Color.White)
                .border(1.dp, DarkGreen, MaterialTheme.shapes.extraSmall)
        ) {
            distanceOptions.forEach { (value, label) ->
                val isSelected = value == selectedDistance
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    },
                    onClick = {
                        onDistanceSelected(value)
                        onExpandedChange(false)
                    },
                    modifier = Modifier.background(if (isSelected) DarkGold else Color.Transparent)
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreenContent(
        paddingValues = PaddingValues(16.dp),
        userLocation = null,
        categories = listOf(
            SearchCategory(Uuid.random(), "Automotive"),
            SearchCategory(Uuid.random(), "Beauty"),
            SearchCategory(Uuid.random(), "Electronics")
        )
    )
}

@Preview
@Composable
fun SearchScreenWithLocationPreview() {
    SearchScreenContent(
        paddingValues = PaddingValues(16.dp),
        userLocation = UserLocation(latitude = 40.7128, longitude = -74.0060),
        categories = listOf(
            SearchCategory(Uuid.random(), "Automotive"),
            SearchCategory(Uuid.random(), "Beauty"),
            SearchCategory(Uuid.random(), "Electronics")
        )
    )
}
