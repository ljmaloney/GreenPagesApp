rootProject.name = "GreenPagesApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://sdk.squareup.com/public/android/")
    }
}

include(":shared")

val isCocoaPodsBuild = System.getenv("PODS_ROOT") != null ||
    settings.startParameter.projectProperties.containsKey("kotlin.native.cocoapods.platform") ||
    settings.startParameter.taskNames.any { it.contains("syncFramework") || it.contains("pod") }

if (!isCocoaPodsBuild) {
    include(":androidApp")
}