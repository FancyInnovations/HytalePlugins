package com.fancyinnovations.runhytale

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.net.URL

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
        val runDir = File(project.projectDir, "run")
        if (!runDir.exists()) runDir.mkdirs()

        val jarFile = File(runDir, "server.jar")

        if (!jarFile.exists()) {
            println("Downloading server jar from ${jarUrl.get()}")
            URL(jarUrl.get()).openStream().use { input ->
                jarFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("Downloaded server jar to ${jarFile.absolutePath}")
        } else {
            println("Server jar already exists at ${jarFile.absolutePath}")
        }

        // Copy the plugin shadowJar output to run/plugins/
        val shadowJarTask = project.tasks.findByName("shadowJar")
        if (shadowJarTask != null) {
            val pluginsDir = File(runDir, "plugins")
            if (!pluginsDir.exists()) pluginsDir.mkdirs()

            val shadowJarFile = shadowJarTask.outputs.files.firstOrNull()
            if (shadowJarFile != null && shadowJarFile.exists()) {
                shadowJarFile.copyTo(File(pluginsDir, shadowJarFile.name), overwrite = true)
                println("Copied plugin jar to ${pluginsDir.absolutePath}")
            }
        }

        println("Running server jar...")

        val process = ProcessBuilder("java", "-jar", jarFile.name)
            .directory(runDir)
            .start()

        // Handle task cancellation
        project.gradle.buildFinished {
            if (process.isAlive) {
                println("Task cancelled. Stopping the server...")
                process.destroy()
            }
        }

        // Thread to read stdout and print it live
        Thread {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { println(it) }
            }
        }.start()

        // Thread to read stderr and print it live
        Thread {
            process.errorStream.bufferedReader().useLines { lines ->
                lines.forEach { System.err.println(it) }
            }
        }.start()

        // Thread to forward console input to process stdin
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