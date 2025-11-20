plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

allprojects {
    group = "com.fancyinnovations"
    version = getFCVersion()
    description = "All the essentials for your Hytale server"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.fancyinnovations.com/snapshots")
        maven(url = "https://repo.lushplugins.org/releases")
    }
}

dependencies {
    implementation(project(":plugins:fancycore:fc-api"))
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.4")
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")
}

tasks {
    shadowJar {
        archiveBaseName.set("FancyCore")
        archiveClassifier.set("")

        dependsOn(":plugins:fancycore:fc-api:shadowJar")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()

        val props = mapOf(
            "description" to project.description,
            "version" to getFCVersion(),
            "commit_hash" to gitCommitHash,
            "channel" to (System.getenv("RELEASE_CHANNEL") ?: "").ifEmpty { "undefined" },
            "platform" to (System.getenv("RELEASE_PLATFORM") ?: "").ifEmpty { "undefined" }
        )
        inputs.properties(props)

        filesMatching("version.yml") {
            expand(props)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

val gitCommitHash: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText.map { it.trim() }

fun getFCVersion(): String {
    return file("VERSION").readText()
}
