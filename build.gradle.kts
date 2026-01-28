plugins {
    id("com.gradleup.shadow") version "9.3.1" apply false
}

allprojects {
    group = "com.fancyinnovations"
    description = "Hytale plugins of FancyInnovations"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven("https://repo.fancyinnovations.com/releases")
        maven("https://repo.fancyinnovations.com/snapshots")
        maven(url = "https://maven.hytale.com/release")
        maven(url = "https://maven.hytale.com/pre-release")
    }
}