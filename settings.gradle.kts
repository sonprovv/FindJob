pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    
    // Define plugin versions here for consistency
    plugins {
        id("com.android.application") version "8.1.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.22" apply false
        id("com.google.dagger.hilt.android") version "2.50" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
        id("com.google.gms.google-services") version "4.4.2" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add any other repositories here if needed
    }
}

rootProject.name = "FindJob"
include(":app")
 