package com.green.yp.app.wizard

enum class ClassifiedWizardStep {
    PACKAGE,
    DETAILS,
    LOCATION,
    CONTACT,
    EMAIL_VALIDATION,
    IMAGES,
    PREVIEW,
    PAYMENT,
    PAYMENT_SUCCESS
}

fun ClassifiedWizardStep.next(): ClassifiedWizardStep =
    when (this) {
        ClassifiedWizardStep.PACKAGE -> ClassifiedWizardStep.DETAILS
        ClassifiedWizardStep.DETAILS -> ClassifiedWizardStep.LOCATION
        ClassifiedWizardStep.LOCATION -> ClassifiedWizardStep.CONTACT
        ClassifiedWizardStep.CONTACT -> ClassifiedWizardStep.EMAIL_VALIDATION
        ClassifiedWizardStep.EMAIL_VALIDATION -> ClassifiedWizardStep.IMAGES
        ClassifiedWizardStep.IMAGES -> ClassifiedWizardStep.PREVIEW
        ClassifiedWizardStep.PREVIEW -> ClassifiedWizardStep.PAYMENT
        ClassifiedWizardStep.PAYMENT -> ClassifiedWizardStep.PAYMENT_SUCCESS
        ClassifiedWizardStep.PAYMENT_SUCCESS -> ClassifiedWizardStep.PAYMENT_SUCCESS
    }

fun ClassifiedWizardStep.previous(): ClassifiedWizardStep =
    when (this) {
        ClassifiedWizardStep.PACKAGE -> ClassifiedWizardStep.PACKAGE
        ClassifiedWizardStep.DETAILS -> ClassifiedWizardStep.PACKAGE
        ClassifiedWizardStep.LOCATION -> ClassifiedWizardStep.DETAILS
        ClassifiedWizardStep.CONTACT -> ClassifiedWizardStep.LOCATION
        ClassifiedWizardStep.EMAIL_VALIDATION -> ClassifiedWizardStep.CONTACT
        ClassifiedWizardStep.IMAGES -> ClassifiedWizardStep.EMAIL_VALIDATION
        ClassifiedWizardStep.PREVIEW -> ClassifiedWizardStep.IMAGES
        ClassifiedWizardStep.PAYMENT -> ClassifiedWizardStep.PAYMENT_SUCCESS
        ClassifiedWizardStep.PAYMENT_SUCCESS -> ClassifiedWizardStep.PAYMENT
    }