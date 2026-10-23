plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:9.4.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.12")
}

gradlePlugin {
    plugins {
        create("androidLibrary") {
            id = "rifa.android.library"
            implementationClass = "com.rifaapp.buildlogic.AndroidLibraryConventionPlugin"
        }
        create("androidApplication") {
            id = "rifa.android.application"
            implementationClass = "com.rifaapp.buildlogic.AndroidApplicationConventionPlugin"
        }
        create("kotlinLibrary") {
            id = "rifa.kotlin.library"
            implementationClass = "com.rifaapp.buildlogic.KotlinLibraryConventionPlugin"
        }
    }
}
