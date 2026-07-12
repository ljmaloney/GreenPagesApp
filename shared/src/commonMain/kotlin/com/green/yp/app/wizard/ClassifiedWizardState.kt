package com.green.yp.app.wizard

import com.green.yp.app.shared.dto.classified.ImageGallery
import kotlin.uuid.Uuid

data class ClassifiedWizardState(val draft: ClassifiedDraft = ClassifiedDraft(),
                                 val currentStep: ClassifiedWizardStep =
                                     ClassifiedWizardStep.PACKAGE,
                                 val listingId: Uuid? = null,
                                 val emailValidated: Boolean = false,
                                 val uploadedImages: List<ImageGallery> = emptyList(),
                                 val loading: Boolean = false,
                                 val error: String? = null)

data class ClassifiedDraft(
    val adType: Uuid? = null,
    val categoryId: Uuid? = null,

    val price: Double? = null,
    val pricePerUnitType: String? = null,

    val firstName: String = "",
    val lastName: String = "",

    val address: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",

    val phoneNumber: String = "",
    val emailAddress: String = "",

    val title: String = "",
    val description: String = ""
)