import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val secretProps = Properties()
val secretFile = rootProject.file("secret.properties")

if (secretFile.exists()) {
    secretFile.inputStream().use { secretProps.load(it) }
}

android {
    namespace = "me.proteus.myeye"
    compileSdk = 35

    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "me.proteus.myeye"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val mapsApiKey =  secretProps.getProperty("MAPS_API_KEY")
        buildConfigField("String", "MAPS_API_KEY", mapsApiKey)
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += arrayOf("META-INF/INDEX.LIST", "META-INF/io.netty.versions.properties")
        }
    }

    sourceSets["main"].assets.srcDirs("src/main/assets")

}

dependencies {

    implementation(libs.sqlite)
    implementation(libs.sqlite.framework)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.biometric)
    implementation(libs.vosk)
    implementation(libs.androidx.appcompat)
    implementation(libs.gson)
    implementation(libs.async.http.client)
    implementation(libs.zip4j)
    implementation(libs.slf4j.simple)
    implementation(libs.core)
    implementation(libs.face.detection)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.maps) {
        exclude("com.android.support")
    }
    implementation(libs.maps.compose)
    implementation(libs.volley)
    implementation(libs.play.services.maps)
    implementation(libs.places.compose)
    implementation(libs.places)
    implementation(libs.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.android)
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
