plugins {
    id("rifa.kotlin.library")
    jacoco
}

tasks.jacocoTestReport {
    classDirectories.setFrom(
        files(classDirectories.files.map { dir ->
            fileTree(dir) {
                exclude("com/rifaapp/core/domain/repository/**")
                exclude("**/*lambda*")
                exclude("**/*ContinuationImpl*")
                exclude("**/*DefaultConstructorMarker*")
                exclude("**/*\$*")
            }
        })
    )
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
