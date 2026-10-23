plugins {
    id("rifa.kotlin.library")
    jacoco
}

tasks.jacocoTestReport {
    classDirectories.setFrom(
        files(classDirectories.files.map { dir ->
            fileTree(dir) {
                exclude("**/*lambda*")
                exclude("**/*ContinuationImpl*")
                exclude("**/*\$*")
                exclude("**/*DefaultConstructorMarker*")
            }
        })
    )
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
