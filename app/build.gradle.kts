plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.openclassroom.eventorias"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.openclassroom.eventorias"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    //Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)

    //kotlin
    implementation(platform(libs.kotlin.bom))

    //DI
    implementation(libs.hilt)
    implementation(libs.core.ktx)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    //Coil
    implementation(libs.coil.compose)
    implementation(libs.accompanist.permissions)

    //Pager
    implementation (libs.accompanist.pager)
    implementation (libs.accompanist.pager.indicators)


    //Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //SplashScreen
    implementation(libs.androidx.core.splashscreen)

    //firebase auth
    implementation(libs.firebase.auth.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation ("com.google.android.gms:play-services-auth:21.3.0")

    //firebase firestore
    implementation ("com.google.firebase:firebase-firestore-ktx:25.1.1")

    //firebase storage
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.appcheck.debug)

    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-storage")


    //firebase messaging FCM
    implementation ("com.google.firebase:firebase-messaging:24.1.0")



    //material
    implementation ("androidx.compose.material:material:1.7.6")

    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")



    //accompanist
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.32.0")

    //Google Map
    implementation ("com.google.android.gms:play-services-maps:19.0.0")


    // ---------------TESTS

    //Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)




    // JUnit (pour les tests unitaires)
    testImplementation("junit:junit:4.13.2")
    // Coroutines Test (pour tester les coroutines)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    // MockK (pour les mocks)
    testImplementation("io.mockk:mockk:1.13.5")

    testImplementation("androidx.arch.core:core-testing:2.2.0")


}