plugins {
    id("rifa.kotlin.library")
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
