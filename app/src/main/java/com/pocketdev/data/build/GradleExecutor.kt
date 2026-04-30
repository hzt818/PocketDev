package com.pocketdev.data.build

import com.pocketdev.domain.model.BuildConfig
import com.pocketdev.domain.model.BuildPhase
import com.pocketdev.domain.model.BuildProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradleExecutor @Inject constructor() {

    private val activeBuilds = mutableMapOf<String, Process>()

    fun execute(projectPath: String, config: BuildConfig): Flow<BuildProgress> = flow {
        val buildId = UUID.randomUUID().toString()
        val projectDir = File(projectPath)

        if (!projectDir.exists()) {
            emit(BuildProgress(buildId, BuildPhase.FAILED, 0, "Project path does not exist: $projectPath"))
            return@flow
        }

        val gradlew = findGradleWrapper(projectDir)
        if (gradlew == null) {
            emit(BuildProgress(buildId, BuildPhase.FAILED, 0, "No Gradle wrapper found"))
            return@flow
        }

        emit(BuildProgress(buildId, BuildPhase.INITIALIZING, 0, "Starting Gradle build..."))

        try {
            val tasks = buildTasks(config)
            val command = listOf(gradlew.absolutePath) + tasks

            val processBuilder = ProcessBuilder(command)
                .directory(projectDir)
                .redirectErrorStream(true)

            val process = processBuilder.start()
            activeBuilds[buildId] = process

            emit(BuildProgress(buildId, BuildPhase.CONFIGURING, 10, "Configuring project..."))

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var currentPhase = BuildPhase.CONFIGURING
            var lastOutput = ""

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let { l ->
                    lastOutput = l

                    val progress = parseProgress(l)
                    val newPhase = parsePhase(l, currentPhase)

                    if (newPhase != currentPhase) {
                        currentPhase = newPhase
                    }
                    emit(BuildProgress(buildId, currentPhase, progress, l))
                }
            }

            val exitCode = process.waitFor()
            activeBuilds.remove(buildId)

            if (exitCode == 0) {
                emit(BuildProgress(buildId, BuildPhase.COMPLETED, 100, "Build successful"))
            } else {
                emit(BuildProgress(buildId, BuildPhase.FAILED, 0, "Build failed with exit code: $exitCode\n$lastOutput"))
            }
        } catch (e: Exception) {
            activeBuilds.remove(buildId)
            emit(BuildProgress(buildId, BuildPhase.FAILED, 0, "Build error: ${e.message}"))
        }
    }

    fun cancel(buildId: String): Boolean {
        activeBuilds[buildId]?.let { process ->
            process.destroyForcibly()
            activeBuilds.remove(buildId)
            return true
        }
        return false
    }

    private fun findGradleWrapper(projectDir: File): File? {
        val unixWrapper = File(projectDir, "gradlew")
        val windowsWrapper = File(projectDir, "gradlew.bat")

        return when {
            unixWrapper.exists() && unixWrapper.canExecute() -> unixWrapper
            windowsWrapper.exists() -> windowsWrapper
            else -> null
        }
    }

    private fun buildTasks(config: BuildConfig): List<String> {
        val tasks = mutableListOf<String>()

        tasks.addAll(config.tasks)

        if (config.cleanBeforeBuild) {
            tasks.add(0, "clean")
        }

        when (config.buildType) {
            "debug" -> tasks.add("-PbuildType=debug")
            "release" -> tasks.add("-PbuildType=release")
        }

        if (config.parallel) {
            tasks.add("--parallel")
        }

        if (config.daemon) {
            tasks.add("--daemon")
        } else {
            tasks.add("--no-daemon")
        }

        config.stacktrace?.let { tasks.add("--stacktrace") }
        config.info?.let { tasks.add("--info") }

        return tasks
    }

    private fun parseProgress(line: String): Int {
        return when {
            line.contains("100%") -> 100
            line.contains("75%") -> 75
            line.contains("50%") -> 50
            line.contains("25%") -> 25
            line.contains("10%") -> 10
            else -> 0
        }
    }

    private fun parsePhase(line: String, currentPhase: BuildPhase): BuildPhase {
        return when {
            line.contains("Downloading") || line.contains("download") -> BuildPhase.CONFIGURING
            line.contains("Running") -> BuildPhase.EXECUTING
            line.contains("Compiling") || line.contains("compile") -> BuildPhase.EXECUTING
            line.contains("Testing") || line.contains("test") -> BuildPhase.EXECUTING
            line.contains("BUILD") && line.contains("SUCCESS") -> BuildPhase.COMPLETED
            line.contains("BUILD") && line.contains("FAILED") -> BuildPhase.FAILED
            line.contains("initializing") -> BuildPhase.INITIALIZING
            line.contains("configuring") -> BuildPhase.CONFIGURING
            line.contains("executing") -> BuildPhase.EXECUTING
            line.contains("finalizing") -> BuildPhase.FINALIZING
            else -> currentPhase
        }
    }
}