plugins {
    id("java-library")
    id("maven-publish")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://repo.fancyinnovations.com/snapshots")
    maven(url = "https://repo.lushplugins.org/releases")
    maven(url = "https://maven.hytale.com/release")
    maven(url = "https://maven.hytale.com/pre-release")
}

dependencies {
    compileOnly("com.hypixel.hytale:Server:2026.01.22-6f8bdbdc4")

    compileOnly("de.oliver.FancyAnalytics:logger:0.0.9")
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.5")
    compileOnly("com.fancyinnovations.fancyspaces:java-sdk:0.0.4")

    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
}

tasks {
    publishing {
        repositories {
            maven {
                name = "fancyinnovationsReleases"
                url = uri("https://repo.fancyinnovations.com/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "fancyinnovationsSnapshots"
                url = uri("https://repo.fancyinnovations.com/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.fancyinnovations.hytale"
                artifactId = "ui-helper"
                version = getUIHelperVersion()
                from(project.components["java"])
            }
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(25)
    }
}

fun getUIHelperVersion(): String {
    return file("VERSION").readText()
}