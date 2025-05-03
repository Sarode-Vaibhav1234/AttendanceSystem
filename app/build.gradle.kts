plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.student_attendance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.student_attendance"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation (libs.volley) // For HTTP request
    implementation (libs.okhttp)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")
    implementation ("com.google.android.material:material:1.11.0")
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.volley)
    implementation (libs.play.services.location)

    implementation (libs.google.material.v1110) // or latest

    implementation (libs.biometric)
    implementation (libs.material.v140)



}