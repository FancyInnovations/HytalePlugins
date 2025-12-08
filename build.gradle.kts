plugins {
    id("com.gradleup.shadow") version "9.3.0" apply false
}

allprojects {
    group = "com.fancyinnovations"
    description = "Hytale plugins of FancyInnovations"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.fancyinnovations.com/releases")
        maven(url = "https://jitpack.io")
    }
}