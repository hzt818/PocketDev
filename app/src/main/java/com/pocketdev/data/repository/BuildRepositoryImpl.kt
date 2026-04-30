package com.pocketdev.data.repository

import com.pocketdev.data.build.GradleExecutor
import com.pocketdev.domain.model.BuildConfig
import com.pocketdev.domain.model.BuildPhase
import com.pocketdev.domain.model.BuildProgress
import com.pocketdev.domain.model.BuildResult
import com.pocketdev.domain.model.GradleInfo
import com.pocketdev.domain.repository.BuildRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildRepositoryImpl @Inject constructor(
    private val gradleExecutor: GradleExecutor
) : BuildRepository {

    private val buildHistory = MutableStateFlow<List<BuildResult>>(emptyList())

    override suspend fun getGradleInfo(projectPath: String): Result<GradleInfo> {
        return try {
            val gradlewFile = File(projectPath, "gradlew")
            val gradleFile = File(projectPath, "gradle")

            val gradleWrapperExists = gradlewFile.exists() && gradlewFile.canExecute()
            val gradleExists = hasGradleInstalled()

            if (!gradleWrapperExists && !gradleExists) {
                return Result.success(
                    GradleInfo(
                        available = false,
                        version = null,
                        homeDir = null,
                        daemonRunning = false
                    )
                )
            }

            val version = getGradleVersion(projectPath, gradleWrapperExists)
            val homeDir = getGradleHomeDir()
            val daemonRunning = checkDaemonRunning(projectPath, gradleWrapperExists)

            Result.success(
                GradleInfo(
                    available = true,
                    version = version,
                    homeDir = homeDir,
                    daemonRunning = daemonRunning
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun executeBuild(config: BuildConfig): Flow<BuildProgress> {
        return gradleExecutor.execute(config.projectPath, config)
    }

    override suspend fun cancelBuild(buildId: String): Result<Unit> {
        return try {
            gradleExecutor.cancel(buildId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBuildHistory(): Flow<List<BuildResult>> {
        return buildHistory
    }

    private fun hasGradleInstalled(): Boolean {
        return try {
            val process = ProcessBuilder("gradle", "--version")
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line?.contains("Gradle") == true) {
                    process.destroyForcibly()
                    return true
                }
            }
            process.destroyForcibly()
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun getGradleVersion(projectPath: String, useWrapper: Boolean): String? {
        return try {
            val command = if (useWrapper) {
                listOf("./gradlew", "--version")
            } else {
                listOf("gradle", "--version")
            }

            val process = ProcessBuilder(command)
                .directory(File(projectPath))
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var version: String? = null

            while (reader.readLine().also { line = it } != null) {
                if (line?.contains("Gradle") == true) {
                    val parts = line?.split(" ") ?: emptyList()
                    if (parts.size >= 2) {
                        version = parts[1]
                    }
                }
            }

            process.destroyForcibly()
            version
        } catch (e: Exception) {
            null
        }
    }

    private fun getGradleHomeDir(): String? {
        return try {
            val process = ProcessBuilder("gradle", "--version")
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var homeDir: String? = null

            while (reader.readLine().also { line = it } != null) {
                if (line?.contains("Gradle home") == true) {
                    homeDir = line?.substringAfter(":")?.trim()
                }
            }

            process.destroyForcibly()
            homeDir
        } catch (e: Exception) {
            null
        }
    }

    private fun checkDaemonRunning(projectPath: String, useWrapper: Boolean): Boolean {
        return try {
            val command = if (useWrapper) {
                listOf("./gradlew", "--status")
            } else {
                listOf("gradle", "--status")
            }

            val process = ProcessBuilder(command)
                .directory(File(projectPath))
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var hasRunningDaemon = false

            while (reader.readLine().also { line = it } != null) {
                if (line?.contains("Daemon") == true &&
                    (line?.contains("running") == true || line?.contains("idle") == true)) {
                    hasRunningDaemon = true
                }
            }

            process.destroyForcibly()
            hasRunningDaemon
        } catch (e: Exception) {
            false
        }
    }
}