plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

android {
    namespace = "com.benyaminrasouli.habitaway"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.benyaminrasouli.habitaway"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    lint {
        abortOnError = false
    }

}

dependencies {
    //دیتابیس
    val room_version = "2.7.2" // آخرین نسخه پایدار

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // --- Core Compose / Activity / Navigation / Foundation / Material3 / Icons ---
    implementation("androidx.activity:activity-compose:1.10.1")        // activity-compose
    implementation("androidx.navigation:navigation-compose:2.9.3")    // navigation
    implementation("androidx.compose.material3:material3")            // material3 (نسخه توسط BOM مدیریت می‌شود)
    implementation("androidx.compose.material:material-icons-extended") // extended icons
    implementation("androidx.compose.foundation:foundation")          // LazyRow, FlowRow و ...

    // --- Coil برای لود عکس‌ها ---
    implementation("io.coil-kt:coil-compose:2.7.0")

    // --- بقیه وابستگی‌های پروژه (با استفاده از catalog اگر داری) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Testing & preview
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.08.01"))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.datastore:datastore-preferences:1.1.7")
// یا آخرین نسخه موجود
    implementation("androidx.compose.foundation:foundation:1.9.0")
// متناسب با نسخه‌ی Compose پروژه‌ات
    implementation("androidx.compose.material3:material3:1.3.2")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")


}
