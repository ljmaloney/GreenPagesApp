package com.green.yp.app.wizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.shared.dto.classified.ClassifiedPaymentResponse
import com.green.yp.app.shared.dto.classified.ClassifiedResponse
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.ui.theme.DarkGreen
import com.green.yp.app.utils.formatCurrency
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun PaymentSuccess(
    response: ClassifiedPaymentResponse,
    modifier: Modifier = Modifier,
    classifiedResponse: ClassifiedResponse? = null,
    referenceViewModel: ClassifiedReferenceViewModel? = null
) {
    val adType = classifiedResponse?.adTypeId?.let { id ->
        referenceViewModel?.getAdTypeById(id)
    }
    val adTypeName = adType?.adTypeName
    val adCost = adType?.monthlyPrice?.let { "$${it.formatCurrency()}" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(80.dp),
            tint = DarkGreen
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Payment Successful!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Thanks for placing your classified ad with us. " +
                        "Below you will find details about your payment.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Please remember to add our domain greenyp.com to your " +
                        "list of approved senders. You should receive a confirmation " +
                        "email within the next few minutes.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Order Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                HorizontalDivider()

                SuccessRow(label = "Ad Title", value = response.classifiedTitle)
                adTypeName?.let { SuccessRow(label = "Ad Type", value = it) }
                adCost?.let { SuccessRow(label = "Ad Cost", value = it) }
                SuccessRow(label = "Order Ref", value = response.orderRef)
                SuccessRow(label = "Payment Ref", value = response.paymentRef)
                SuccessRow(label = "Receipt #", value = response.receiptNumber)
                SuccessRow(label = "Status", value = response.paymentStatus)
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
fun PaymentSuccessPreview() {
    val mockResponse = ClassifiedPaymentResponse(
        classifiedId = Uuid.random(),
        classifiedTitle = "Premium Garden Soil Ad",
        paymentStatus = "COMPLETED",
        paymentRef = "PAY-67890",
        orderRef = "ORD-12345",
        receiptNumber = "RCPT-001",
        errorStatusCode = "200",
        errorDetail = "None"
    )
    MaterialTheme {
        PaymentSuccess(
            response = mockResponse
        )
    }
}

@Composable
private fun SuccessRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
