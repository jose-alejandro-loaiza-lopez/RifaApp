plugins {
    id("rifa.android.library")
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.rifaapp.core.database"
    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(project(":core:domain"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.coroutines.core)
}
