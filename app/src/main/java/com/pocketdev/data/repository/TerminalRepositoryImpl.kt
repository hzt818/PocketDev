package com.pocketdev.data.repository

import com.pocketdev.domain.model.PcShellRequest
import com.pocketdev.domain.model.TerminalOutput
import com.pocketdev.domain.model.TerminalSession
import com.pocketdev.domain.model.TerminalSize
import com.pocketdev.domain.model.ShellType
import com.pocketdev.domain.repository.PcConnectionRepository
import com.pocketdev.domain.repository.TerminalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalRepositoryImpl @Inject constructor(
    private val pcConnectionRepository: PcConnectionRepository
) : TerminalRepository {

    private val _activeSessions = MutableStateFlow<List<TerminalSession>>(emptyList())
    private val sessionOutputs = mutableMapOf<String, Channel<TerminalOutput>>()
    private val processMap = mutableMapOf<String, Process>()

    override suspend fun createSession(cwd: String?, shellType: ShellType): Result<TerminalSession> = withContext(Dispatchers.IO) {
        try {
            val sessionId = UUID.randomUUID().toString()
            val workingDir = cwd ?: System.getProperty("user.dir") ?: "/"

            val outputChannel = Channel<TerminalOutput>(Channel.BUFFERED)
            sessionOutputs[sessionId] = outputChannel

            if (shellType == ShellType.LOCAL) {
                val processBuilder = ProcessBuilder("/system/bin/sh")
                processBuilder.directory(java.io.File(workingDir))
                processBuilder.redirectErrorStream(false)

                val process = processBuilder.start()
                processMap[sessionId] = process

                val pid = process.hashCode()

                val session = TerminalSession(
                    id = sessionId,
                    pid = pid,
                    cwd = workingDir,
                    startedAt = System.currentTimeMillis(),
                    isActive = true,
                    shellType = ShellType.LOCAL
                )

                // Launch reader coroutine for process output
                launch {
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    try {
                        var line: String? = reader.readLine()
                        while (line != null && isActive) {
                            outputChannel.send(
                                TerminalOutput(
                                    sessionId = sessionId,
                                    data = "$line\n",
                                    isError = false,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            line = reader.readLine()
                        }
                    } catch (e: Exception) {
                        // Session ended
                    } finally {
                        reader.close()
                    }
                }

                // Launch reader coroutine for stderr
                launch {
                    val errorReader = BufferedReader(InputStreamReader(process.errorStream))
                    try {
                        var line: String? = errorReader.readLine()
                        while (line != null && isActive) {
                            outputChannel.send(
                                TerminalOutput(
                                    sessionId = sessionId,
                                    data = "$line\n",
                                    isError = true,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            line = errorReader.readLine()
                        }
                    } catch (e: Exception) {
                        // Session ended
                    } finally {
                        errorReader.close()
                    }
                }

                // Update active sessions
                _activeSessions.value = _activeSessions.value + session

                Result.success(session)
            } else {
                // Remote shell session - check for active connection
                val activeConnection = pcConnectionRepository.activeConnectionFlow.first()
                if (activeConnection == null) {
                    sessionOutputs.remove(sessionId)
                    return@withContext Result.failure(Exception("No active PC connection. Please connect to a PC first."))
                }

                val session = TerminalSession(
                    id = sessionId,
                    pid = sessionId.hashCode(),
                    cwd = activeConnection.host,
                    startedAt = System.currentTimeMillis(),
                    isActive = true,
                    shellType = ShellType.REMOTE
                )

                _activeSessions.value = _activeSessions.value + session
                Result.success(session)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun writeToSession(sessionId: String, input: String) = withContext(Dispatchers.IO) {
        val session = _activeSessions.value.find { it.id == sessionId } ?: return@withContext

        if (session.shellType == ShellType.LOCAL) {
            val process = processMap[sessionId] ?: return@withContext
            try {
                val writer = OutputStreamWriter(process.outputStream)
                val inputWithNewline = if (!input.endsWith("\n")) "$input\n" else input
                writer.write(inputWithNewline)
                writer.flush()
            } catch (e: Exception) {
                val channel = sessionOutputs[sessionId]
                channel?.send(
                    TerminalOutput(
                        sessionId = sessionId,
                        data = "Error writing to terminal: ${e.message}\n",
                        isError = true,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        } else {
            // Remote shell execution
            val channel = sessionOutputs[sessionId] ?: return@withContext
            try {
                val result = pcConnectionRepository.executeShell(PcShellRequest(command = input, cwd = session.cwd))
                result.fold(
                    onSuccess = { output ->
                        channel.send(
                            TerminalOutput(
                                sessionId = sessionId,
                                data = output,
                                isError = false,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    },
                    onFailure = { error ->
                        channel.send(
                            TerminalOutput(
                                sessionId = sessionId,
                                data = "Error: ${error.message}\n",
                                isError = true,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                )
            } catch (e: Exception) {
                channel.send(
                    TerminalOutput(
                        sessionId = sessionId,
                        data = "Error: ${e.message}\n",
                        isError = true,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    override fun readOutput(sessionId: String): Flow<TerminalOutput> {
        val channel = sessionOutputs[sessionId]
        return channel?.consumeAsFlow() ?: kotlinx.coroutines.flow.flowOf()
    }

    override suspend fun resize(sessionId: String, size: TerminalSize) {
        // Android does not support PTY resize natively
        // This is a no-op for basic shell execution
    }

    override suspend fun closeSession(sessionId: String) = withContext(Dispatchers.IO) {
        try {
            processMap[sessionId]?.destroy()
            processMap.remove(sessionId)
            sessionOutputs[sessionId]?.close()
            sessionOutputs.remove(sessionId)

            _activeSessions.value = _activeSessions.value.filter { it.id != sessionId }
        } catch (e: Exception) {
            // Cleanup errors are ignored
        }
    }

    override fun getActiveSessions(): Flow<List<TerminalSession>> = _activeSessions.asStateFlow()
}