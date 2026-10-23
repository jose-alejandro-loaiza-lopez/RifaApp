plugins {
    id("rifa.android.library")
    alias(libs.plugins.compose)
}

android {
    namespace = "com.rifaapp.core.designsystem"
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:domain"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
