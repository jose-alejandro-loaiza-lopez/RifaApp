package com.rifaapp.buildlogic

import com.android.build.gradle.internal.dsl.LibraryExtensionImpl
import org.gradle.api.JavaVersion

fun LibraryExtensionImpl.configureBasic() {
    compileSdk = 37
    defaultConfig.minSdk = 24
    compileOptions.isCoreLibraryDesugaringEnabled = true
    compileOptions.sourceCompatibility = JavaVersion.VERSION_17
    compileOptions.targetCompatibility = JavaVersion.VERSION_17
}
