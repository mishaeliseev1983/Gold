plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")

}

android {
    namespace = "it.gb.sportivo"
    compileSdk = 33

    defaultConfig {
        applicationId = "it.gb.sportivo"
        minSdk = 23
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    val glideVersion = "4.14.2"
    implementation("com.github.bumptech.glide:glide:$glideVersion")

    //dagger
    implementation("com.google.dagger:dagger:2.48")
    kapt("com.google.dagger:dagger-compiler:2.48")

    val lifecycle = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycle}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${lifecycle}")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.google.android.flexbox:flexbox:3.0.0")

}