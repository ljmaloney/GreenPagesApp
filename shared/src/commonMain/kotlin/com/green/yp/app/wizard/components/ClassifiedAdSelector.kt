package com.green.yp.app.wizard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.dto.classified.ClassifiedAdType
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.ui.theme.DarkGreen

@Composable
fun ClassifiedAdSelector(
    viewModel: ClassifiedReferenceViewModel,
    onAdTypeSelected: (ClassifiedAdType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
        
        ClassifiedAdTypeSelector(
            viewModel = viewModel,
            onAdTypeSelected = onAdTypeSelected
        )
    }
}
