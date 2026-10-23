package com.rifaapp.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("java-library")

            val kotlinExt = extensions.findByName("kotlin") as org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
            kotlinExt.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

            val javaExt = extensions.findByName("java") as org.gradle.api.plugins.JavaPluginExtension
            javaExt.sourceCompatibility = JavaVersion.VERSION_17
            javaExt.targetCompatibility = JavaVersion.VERSION_17
        }
    }
}
