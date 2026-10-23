pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "RifaApp"

include(":app")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":feature:rifa")
include(":feature:config")
