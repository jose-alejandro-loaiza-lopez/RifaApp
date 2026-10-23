package com.rifaapp.buildlogic

import com.android.build.gradle.internal.dsl.LibraryExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            extensions.configure<LibraryExtensionImpl>("android") {
                configureBasic()
                buildFeatures.compose = false
            }
        }
    }
}

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            val extension = extensions.getByName("android")
            // intentionally no-op; app module keeps explicit config
        }
    }
}
