import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.jetbrains.kotlin.kover)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.dagger.hilt.android)
}

android {
    namespace = "io.github.athorfeo.template"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.athorfeo.template"
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
        debug {
            isDebuggable = true
            buildConfigField("String", "BASE_URL", "${project.properties["debug_base_url"]}")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.lifecycle.runtime)

    implementation(libs.compose.hilt)

    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //Coroutines
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Retrofit
    implementation(libs.bundles.retrofit)

    // Gson
    implementation(libs.gson)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // Test
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockk.android)
    testImplementation(libs.test.mockk.agent)

    androidTestImplementation(libs.test.mockk.android)
    androidTestImplementation(libs.test.mockk.agent)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(files("${rootProject.rootDir}/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(false)
        html.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

val koverFilesExcludes = listOf(
    "*Activity*",
    "*Activity_*",
    "*Activity\$*",
    "*BuilConfig*",
    "*_di_*",
    "*.di.*Module*",
    "*_navigation_*",
    "*Module_*",
    "*module_*",
    "*Hilt_*",
    "*_Hilt*",
    "*_Factory*",
    "*DaggerInjectableComponent*",
    "*ComposableSingletons*",
    "*TemplateApp*",
    "*TemplateApp_*",
    "*_TemplateApp*",
)
kover {
    reports{
        filters{
            excludes {
                androidGeneratedClasses()
                classes(koverFilesExcludes)
                annotatedBy("androidx.compose.runtime.Composable")
            }
        }

        verify {
            rule {
                bound {
                    minValue = 80
                }
            }
        }
    }
}