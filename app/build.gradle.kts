import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {

    namespace = "com.openclassroom.eventorias"
    compileSdk = 34


    val secretPropertiesFile = rootProject.file("secrets.properties")
    if (secretPropertiesFile.exists()) {
        val properties = Properties().apply {
            load(secretPropertiesFile.inputStream())
        }
        signingConfigs {
            create("release") {
                storeFile = file(properties.getProperty("KEYSTORE_FILE"))
                storePassword =  properties.getProperty("KEYSTORE_PASSWORD")
                keyAlias = properties.getProperty("KEY_ALIAS")
                keyPassword = properties.getProperty("KEY_PASSWORD")
            }
        }
    } else { throw GradleException("secrets.properties file not found. Please ensure it's present in the project root.") }



    defaultConfig {
        applicationId = "com.openclassroom.eventorias"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.openclassroom.eventorias.TestConfig.CustomTestRunner"
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
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources.excludes.addAll(listOf("/META-INF/{AL2.0,LGPL2.1}","/META-INF/LICENSE.md","/META-INF/LICENSE-notice.md"))

    }

    secrets {

        propertiesFileName = "secrets.properties"
        defaultPropertiesFileName = "local.properties"

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
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.junit.ktx)
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

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)




    //hilt tests
    androidTestImplementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")


    androidTestImplementation("io.mockk:mockk-android:1.13.3")
    androidTestImplementation("io.mockk:mockk:1.13.3")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.6")
    androidTestImplementation ("androidx.compose.ui:ui-tooling:1.7.6")
    androidTestImplementation ("androidx.compose.ui:ui-test:1.7.6")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.7.6")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.7.6")

    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")


    androidTestImplementation("androidx.test.ext:junit-ktx:1.2.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")



    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    testImplementation(libs.junit)



    // JUnit (pour les tests unitaires)
    testImplementation("junit:junit:4.13.2")
    // Coroutines Test (pour tester les coroutines)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    // MockK (pour les mocks)
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("androidx.arch.core:core-testing:2.2.0")


}