plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt.android)
    id ("kotlin-kapt")
    id ("kotlin-parcelize")
    alias(libs.plugins.google.firebase.crashlytics)

}

android {
    namespace = "com.cyberiyke.fintech"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cyberiyke.fintech"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation (libs.timber)



    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.firebase.crashlytics)
    kapt(libs.hilt.android.compiler)

    //glide
    implementation (libs.glide)


    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //lifecycle
    implementation (libs.androidx.lifecycle.runtime.ktx) // Use the latest version


    // coil
    implementation (libs.coil)
    kapt(libs.androidx.room.compiler)

    //Coroutines
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)


    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}