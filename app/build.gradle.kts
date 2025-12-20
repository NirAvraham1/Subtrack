plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.subtrack"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.subtrack"
        minSdk = 24
        targetSdk = 36
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Room ---
    val roomVersion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion") // עכשיו ksp אמור לעבוד

    // --- Hilt ---
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48") // עכשיו kapt אמור לעבוד

    // --- Navigation ---
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.5")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // --- WorkManager (לתזמון משימות ברקע) ---
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // --- Hilt תמיכה ב-WorkManager ---
    implementation("androidx.hilt:hilt-work:1.2.0")
    // (הערה: את kapt של hilt-compiler כבר יש לך, אז לא צריך להוסיף שוב)

    kapt("androidx.hilt:hilt-compiler:1.2.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-ads:23.3.0")

    implementation("androidx.cardview:cardview:1.0.0")
}