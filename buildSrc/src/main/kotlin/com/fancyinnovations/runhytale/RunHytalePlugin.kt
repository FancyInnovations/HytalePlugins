package com.fancyinnovations.runhytale

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import java.io.File

open class RunHytalePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("runHytale", RunHytaleExtension::class.java)

        val runTask: TaskProvider<RunServerTask> = project.tasks.register("runServer", RunServerTask::class.java) {
            jarUrl.set(extension.jarUrl)
            group = "run-hytale"
            description = "Downloads and runs the Hytale server jar."
        }

        project.tasks.findByName("shadowJar")?.let {
            runTask.configure {
                dependsOn(it)
            }
        }
    }
}

open class RunHytaleExtension {
    var jarUrl: String = "https://example.com/server.jar"
}

open class RunServerTask : DefaultTask() {

    @Input
    val jarUrl = project.objects.property(String::class.java)

    @TaskAction
    fun run() {
        val runDir = File(project.projectDir, "run").apply { mkdirs() }
        val pluginsDir = File(runDir, "mods").apply { mkdirs() }
        val jarFile = File(runDir, "server.jar")

        // Cache folder
//        val cacheDir = File(project.layout.buildDirectory.asFile.get(), "hytale-cache").apply { mkdirs() }
//
//        // Compute a hash from the URL for caching
//        val urlHash = MessageDigest.getInstance("SHA-256")
//            .digest(jarUrl.get().toByteArray())
//            .joinToString("") { "%02x".format(it) }
//        val cachedJar = File(cacheDir, "$urlHash.jar")
//
//        // Download if cache is missing
//        if (!cachedJar.exists()) {
//            println("Downloading server jar from ${jarUrl.get()}")
//            URI.create(jarUrl.get()).toURL().openStream().use { input ->
//                cachedJar.outputStream().use { output ->
//                    input.copyTo(output)
//                }
//            }
//            println("Downloaded server jar to cache: ${cachedJar.absolutePath}")
//        } else {
//            println("Using cached server jar: ${cachedJar.absolutePath}")
//        }
//
//        // Copy jar to run directory
//        cachedJar.copyTo(jarFile, overwrite = true)
//        println("Copied server jar")

        // Copy plugin shadowJar output
        project.tasks.findByName("shadowJar")?.outputs?.files?.firstOrNull()?.let { shadowJar ->
            shadowJar.copyTo(File(pluginsDir, shadowJar.name), overwrite = true)
            println("Copied plugin jar")
        }

        println("Running server jar ...")

        val process = ProcessBuilder("java", "-jar", jarFile.name, "--assets", "assets.zip")
            .directory(runDir)
            .start()

        // Stop server if task is canceled
        project.gradle.buildFinished {
            if (process.isAlive) {
                println("Task cancelled. Stopping the server...")
                process.destroy()
            }
        }

        // stdout
        Thread {
            process.inputStream.bufferedReader().useLines { it ->
                it.forEach {
                    println(it)
                }
            }
        }.start()

        // stderr
        Thread {
            process.errorStream.bufferedReader().useLines { it ->
                it.forEach {
                    System.err.println(it)
                }
            }
        }.start()

        // stdin
        Thread {
            System.`in`.bufferedReader().useLines { lines ->
                lines.forEach {
                    process.outputStream.write((it + "\n").toByteArray())
                    process.outputStream.flush()
                }
            }
        }.start()

        val exitCode = process.waitFor()
        println("Server exited with code $exitCode")
    }
}