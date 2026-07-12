import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.buildKonfig)
    kotlin("native.cocoapods")
}

buildkonfig {
    packageName = "com.green.yp.app.config"
    val serviceUrl = project.findProperty("GREENYP_SERVICE_URL")?.toString() ?: "https://services.greenyp.com/"
    val squareId = project.findProperty("SQUARE_APPLICATION_ID")?.toString() ?: "sandbox-sq0idb-M2aZ-sHnLqx0tFnGEbgTbw"
    val locationId = project.findProperty("SQUARE_LOCATION_ID")?.toString() ?: "LG1AG21E6AZ4T"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "GREENYP_SERVICE_URL", serviceUrl)
        buildConfigField(FieldSpec.Type.STRING, "SQUARE_APPLICATION_ID", squareId)
        buildConfigField(FieldSpec.Type.STRING, "SQUARE_LOCATION_ID", locationId)
    }
}


kotlin {

    iosArm64()
    iosSimulatorArm64()

//    iosArm64().binaries.framework {
//        baseName = "Shared"
//        isStatic = false
//    }
//
//    iosSimulatorArm64().binaries.framework {
//        baseName = "Shared"
//        isStatic = false
//    }

    cocoapods {
        summary = "GreenYP shared module"
        homepage = "https://greenyp.com"

        version = "1.0"
        ios.deploymentTarget = "16.0"

        framework {
            baseName = "Shared"
            isStatic = false
        }
        pod("SquareInAppPaymentsSDK") {
            version = "1.6.7"
        }

        pod("SquareBuyerVerificationSDK") {
            version = "1.6.7"
        }
    }
    
    androidLibrary {
       namespace = "com.green.yp.app.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.google.playServices.location)
            implementation(libs.ktor.client.okhttp)
            api(libs.square.card.entry)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktorfit.lib)
            implementation(libs.ktorfit.converters.response)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspCommonMainMetadata", libs.ktorfit.lib)
    add("kspAndroid", libs.ktorfit.lib)
    add("kspIosArm64", libs.ktorfit.lib)
    add("kspIosSimulatorArm64", libs.ktorfit.lib)
}