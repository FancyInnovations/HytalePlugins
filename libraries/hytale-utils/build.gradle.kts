plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    compileOnly("com.hypixel.hytale:Server:2026.01.28-87d03be09")

    compileOnly("de.oliver.FancyAnalytics:logger:0.0.9")

    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("org.mongodb:mongodb-driver-sync:5.6.3")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
}

tasks {
    publishing {
        repositories {
            maven {
                name = "fancyspacesReleases"
                url = uri("https://maven.fancyspaces.net/fancyinnovations/releases")

                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = providers
                        .gradleProperty("fancyspacesApiKey")
                        .orElse(
                            providers
                                .environmentVariable("FANCYSPACES_API_KEY")
                                .orElse("")
                        )
                        .get()
                }

                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }

            maven {
                name = "fancyspacesSnapshots"
                url = uri("https://maven.fancyspaces.net/fancyinnovations/snapshots")

                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = providers
                        .gradleProperty("fancyspacesApiKey")
                        .orElse(
                            providers
                                .environmentVariable("FANCYSPACES_API_KEY")
                                .orElse("")
                        )
                        .get()
                }

                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }

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
                artifactId = "hytale-utils"
                version = getHytaleUtilsVersion()
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

fun getHytaleUtilsVersion(): String {
    return file("VERSION").readText()
}