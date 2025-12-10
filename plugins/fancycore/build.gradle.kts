plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
    id("run-hytale")
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
    compileOnly(files("../../libraries/hytale-server/server.jar"))

    implementation(project(":plugins:fancycore:fc-api"))

    implementation("de.oliver:JDB:1.0.4")
    implementation("de.oliver:config:1.0.0")
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.5")
    implementation("de.oliver.FancyAnalytics:logger:0.0.9")

    compileOnly("com.google.code.gson:gson:2.13.2")
    implementation("org.jetbrains:annotations:26.0.2-1")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

runHytale {
    jarUrl = "https://fill-data.papermc.io/v1/objects/d5f47f6393aa647759f101f02231fa8200e5bccd36081a3ee8b6a5fd96739057/paper-1.21.10-115.jar"
}

tasks {
    shadowJar {
        archiveBaseName.set("FancyCore")
        archiveClassifier.set("")

        dependsOn(":plugins:fancycore:fc-api:shadowJar")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 24
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

    test {
        useJUnitPlatform()
    }

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(24))
}

val gitCommitHash: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText.map { it.trim() }

fun getFCVersion(): String {
    return file("VERSION").readText()
}
