import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
        maven(url = "https://maven.hytale.com/release")
        maven(url = "https://maven.hytale.com/pre-release")
    }
}

dependencies {
    compileOnly("com.hypixel.hytale:Server:2026.01.22-6f8bdbdc4")
//    compileOnly(files("../../libraries/hytale-server/HytaleServer.jar"))

    implementation(project(":plugins:fancycore:fc-api"))

    implementation(project(":libraries:version-checker"))
//    implementation(project(":libraries:ui-helper"))

    implementation("de.oliver:JDB:1.0.4")
    implementation("de.oliver:config:1.0.1")
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.5")
    implementation("de.oliver.FancyAnalytics:logger:0.0.9")
    implementation("com.fancyinnovations.fancyspaces:java-sdk:0.0.4")

    compileOnly("net.luckperms:api:5.5")

    compileOnly("com.google.code.gson:gson:2.13.2")
    implementation("org.mongodb:mongodb-driver-sync:5.6.2")
    implementation("org.jetbrains:annotations:26.0.2-1")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<Copy>("copyShadowJar") {
    group = "hytale"
    val shadowJarTask = tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJarTask)

    from(shadowJarTask.flatMap { it.archiveFile })
    into(file("run/mods"))
}

tasks.register<JavaExec>("runServer") {
    group = "hytale"
    description = "Runs the Hytale server from the run directory using Java 25 toolchain"

    dependsOn("copyShadowJar")

    workingDir = file("run")

    classpath = files("HytaleServer.jar")

    mainClass.set("-jar")

    args(
        "HytaleServer.jar",
        "--assets",
        "Assets.zip",
        "--disable-sentry"
    )

//    jvmArgs("-XX:AOTCache=HytaleServer.aot")

    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    )

    standardInput = System.`in`
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
            "commit" to "N/A",
            "channel" to (System.getenv("RELEASE_CHANNEL") ?: "").ifEmpty { "undefined" },
        )
        inputs.properties(props)

        filesMatching("version.json") {
            expand(props)
        }
        filesMatching("manifest.json") {
            expand(props)
        }
    }

    test {
        useJUnitPlatform()
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
