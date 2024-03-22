plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.fetchtriviaquestions"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fetchtriviaquestions"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    implementation ("com.google.mlkit:translate:17.0.2")
   // implementation ("com.google.mlkit:language-id:17.0.2")
   // implementation ("com.google.mlkit:translate-models:17.0.2")
   // implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}