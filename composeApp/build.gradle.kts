import io.github.frankois944.spmForKmp.definition.SwiftDependency
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

val system: String = System.getProperty("os.name")
val isMacos = system.contains("mac", true)

if (isMacos) {
    logger.warn("You are running non-Apple buildscript on an Apple device (${system}). " +
            "To include iOS builds, replace this file with the build.gradle.mac.kts")
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    val iosTargets = listOf(iosSimulatorArm64(), /* iosX64(), iosArm64(),*/)

    iosTargets.forEach { target ->
        target.compilations {
            val main by getting {
                cinterops {
                    create("swiftSrc")
                }
            }
        }
    }

    androidTarget {
        compilerOptions {
            println("JVM target set to " + jvmTarget.get().target)
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets {

        applyDefaultHierarchyTemplate()

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("com.google.android.libraries.maps:maps:3.1.0-beta") {
                exclude("com.android.support")
            }
            implementation(libs.maps.compose)
            implementation(libs.volley)
            implementation(libs.play.services.maps)
            implementation(libs.places.compose)
            implementation(libs.places)
            implementation(libs.vosk)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.mlkit.face)
        }

        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api(libs.biometry)
                api(libs.biometry.compose)
                implementation(libs.lifecycle.viewmodel)
                implementation(libs.lifecycle.runtime.compose)
                implementation(libs.navigation.compose)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.sqlite)
                implementation(libs.korio)
                implementation(libs.korim)
                implementation(libs.permissions.microphone)
                implementation(libs.permissions.camera)
                api(libs.permissions)
                api(libs.permissions.compose)
            }
        }

        androidMain.get().dependsOn(mobileMain)

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor3)
        }
    }
}
compose.web {
//    println(resources.text)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "me.proteus.myeye.resources"
    generateResClass = auto
}

android {
    namespace = "me.proteus.myeye"
    compileSdk = 35

    defaultConfig {
        applicationId = "me.proteus.myeye"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}