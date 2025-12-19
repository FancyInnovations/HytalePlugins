plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.9")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
    compileOnly("com.fancyinnovations.fancyspaces:java-sdk:0.0.2")
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
                artifactId = "version-checker"
                version = getVersionCheckerVersion()
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

fun getVersionCheckerVersion(): String {
    return file("VERSION").readText()
}