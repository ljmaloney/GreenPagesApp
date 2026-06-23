package com.green.yp.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.green.yp.app.ui.theme.DarkGreen

@Preview
@Composable
fun SearchScreen(
    paddingValues: PaddingValues = PaddingValues(16.dp)
) {
    val locationManager = remember { getLocationManager() }
    val userLocation by locationManager.locationUpdates.collectAsState()

    SearchScreenContent(
        paddingValues = paddingValues,
        userLocation = userLocation
    )
}

@Composable
fun SearchScreenContent(
    paddingValues: PaddingValues,
    userLocation: UserLocation?
) {
    var keywords by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var selectedDistance by remember { mutableStateOf(10) }
    var distanceExpanded by remember { mutableStateOf(false) }
    var categoryRef by remember { mutableStateOf("") }

    val distanceOptions = listOf(
        10 to "10 miles",
        25 to "25 miles",
        50 to "50 miles",
        100 to "100 miles"
    )

    val isZipCodeRequired = userLocation == null
    val isFormValid = selectedDistance > 0 && (zipCode.isNotEmpty() || userLocation != null)

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
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp),
            color = DarkGreen
        )

        // Keywords TextField
        OutlinedTextField(
            value = keywords,
            onValueChange = { keywords = it },
            label = { Text("Keywords (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedLabelColor = DarkGreen,
                focusedLabelColor = DarkGreen,
                unfocusedTextColor = DarkGreen,
                focusedTextColor = DarkGreen
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

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
            colors = TextFieldDefaults.colors(
                unfocusedLabelColor = DarkGreen,
                focusedLabelColor = DarkGreen,
                unfocusedTextColor = DarkGreen,
                focusedTextColor = DarkGreen
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Distance Dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { distanceExpanded = !distanceExpanded }
        ) {
            DistanceDropdown(
                selectedDistance = selectedDistance,
                onDistanceSelected = { selectedDistance = it },
                distanceOptions = distanceOptions,
                isExpanded = distanceExpanded,
                onExpandedChange = { distanceExpanded = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Category Ref TextField (Placeholder, Disabled)
        OutlinedTextField(
            value = categoryRef,
            onValueChange = { categoryRef = it },
            label = { Text("Category (Coming soon)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = false,
            placeholder = { Text("Placeholder - Feature coming soon") },
            colors = TextFieldDefaults.colors(
                unfocusedLabelColor = DarkGreen,
                focusedLabelColor = DarkGreen,
                unfocusedTextColor = DarkGreen,
                focusedTextColor = DarkGreen,
                disabledTextColor = DarkGreen,
                disabledLabelColor = DarkGreen)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Search Button
        Button(
            onClick = { /* Handle search */ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            enabled = isFormValid,
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Search")
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
    Column(modifier = Modifier.fillMaxWidth()) {
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
            colors = TextFieldDefaults.colors(
                unfocusedLabelColor = DarkGreen,
                focusedLabelColor = DarkGreen,
                unfocusedTextColor = DarkGreen,
                focusedTextColor = DarkGreen,
                disabledTextColor = DarkGreen,
                disabledLabelColor = DarkGreen
            )
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            distanceOptions.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onDistanceSelected(value)
                        onExpandedChange(false)
                    }
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
        userLocation = null
    )
}

@Preview
@Composable
fun SearchScreenWithLocationPreview() {
    SearchScreenContent(
        paddingValues = PaddingValues(16.dp),
        userLocation = UserLocation(latitude = 40.7128, longitude = -74.0060)
    )
}
