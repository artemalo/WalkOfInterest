import java.util.Properties

val serverUrl: String by extra {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    val value = properties.getProperty("SERVER_URL", "http://192.168.0.149:8080") // 10.0.2.2
    if (value.isEmpty()) {
        throw InvalidUserDataException("Server URL is not provided")
    }
    value
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "sfedu.ictis.walkOfInterest"
    compileSdk = 36

    buildFeatures {
        buildConfig = true

        dataBinding = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "sfedu.ictis.walkOfInterest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"$serverUrl\"")
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
}

dependencies {
    // security
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
    //room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:${room_version}")
    implementation("androidx.room:room-ktx:${room_version}")
    ksp("androidx.room:room-compiler:$room_version")

    implementation("com.google.code.gson:gson:2.10.1")
    //koin
    implementation("io.insert-koin:koin-android:3.5.0")
    //
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation(libs.osmdroid.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
