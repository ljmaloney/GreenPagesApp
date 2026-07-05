package com.green.yp.app.wizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.ui.theme.DarkGreen
import kotlin.uuid.Uuid

@Composable
fun ClassifiedAdSelector(
    viewModel: ClassifiedReferenceViewModel,
    selectedAdType: Uuid?,
    onAdTypeSelected: (ClassifiedAdType) -> Unit,
    modifier: Modifier = Modifier
) {
    val adTypes by viewModel.adTypes.collectAsState()

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "1. Select classified ad package",
            style = MaterialTheme.typography.titleLarge,
            color = DarkGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (adTypes.isNotEmpty()) {
            ClassifiedAdTypeSelectorContent(
                adTypes = adTypes,
                selectedId = selectedAdType,
                onAdTypeSelected = onAdTypeSelected
            )
        }
    }
}
