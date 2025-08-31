plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.cpx.habitaway"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cpx.habitaway"
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

    // ===== حتماً این رو اضافه کن =====
    composeOptions {
        // مقدار زیر یکی از ورژن‌های متداول کامپایلر است؛ اگه با BOMت همخوانی نداره، مقدار مناسب رو انتخاب کن
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // --- Compose BOM (یکبار، مدیریت نسخه‌ها رو BOM انجام میده) ---
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    // --- Core Compose / Activity / Navigation / Foundation / Material3 / Icons ---
    implementation("androidx.activity:activity-compose:1.8.0")        // activity-compose
    implementation("androidx.navigation:navigation-compose:2.7.0")    // navigation
    implementation("androidx.compose.material3:material3")            // material3 (نسخه توسط BOM مدیریت می‌شود)
    implementation("androidx.compose.material:material-icons-extended") // extended icons
    implementation("androidx.compose.foundation:foundation")          // LazyRow, FlowRow و ...

    // --- Coil برای لود عکس‌ها ---
    implementation("io.coil-kt:coil-compose:2.6.0")

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
// یا ورژن مناسب پروژه‌ات
}
