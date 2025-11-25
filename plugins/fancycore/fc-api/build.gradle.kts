plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

dependencies {
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.8")
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
                artifactId = "FancyCore"
                version = getFCVersion()
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

        options.release.set(24)
    }
}

fun getFCVersion(): String {
    return file("../VERSION").readText()
}