plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pictopalette"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pictopalette"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Añade este bloque para definir los placeholders del manifest
        manifestPlaceholders["redirectSchemeName"] = "tuapp" // Reemplaza "tuapp" si usaste otro esquema
        manifestPlaceholders["redirectHostName"] = "callback" // Reemplaza "callback" si usaste otro host
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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.palette)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation(libs.play.services.auth)

    // Retrofit para hacer las peticiones HTTP a Unsplash
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // Picasso
    implementation (libs.picasso)

    // Spotify
    implementation ("com.spotify.android:auth:2.0.2")

    // Glide para cargar imágenes en los ImageViews
    implementation (libs.glide.v4120)
    annotationProcessor (libs.compiler)

    implementation(libs.github.glide)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}