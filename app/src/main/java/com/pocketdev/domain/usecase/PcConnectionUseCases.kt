package com.pocketdev.domain.usecase

import com.pocketdev.domain.model.PcConnectionConfig
import com.pocketdev.domain.model.PcFileReadRequest
import com.pocketdev.domain.model.PcFileWriteRequest
import com.pocketdev.domain.model.PcGitCommitRequest
import com.pocketdev.domain.model.PcShellRequest
import com.pocketdev.domain.repository.PcConnectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPcConnectionsUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    val connectionsFlow: Flow<List<PcConnectionConfig>> = repository.connectionsFlow
    val activeConnectionFlow: Flow<PcConnectionConfig?> = repository.activeConnectionFlow

    suspend fun getConnections(): List<PcConnectionConfig> {
        return repository.getConnections()
    }
}

class AddPcConnectionUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(config: PcConnectionConfig): Result<Unit> {
        return repository.addConnection(config)
    }
}

class RemovePcConnectionUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.removeConnection(id)
    }
}

class SetActivePcConnectionUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.setActiveConnection(id)
    }
}

class TestPcConnectionUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        return repository.testConnection(id)
    }
}

class ReadPcFileUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(path: String): Result<String> {
        return repository.readFile(PcFileReadRequest(path))
    }
}

class WritePcFileUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(path: String, content: String): Result<Unit> {
        return repository.writeFile(PcFileWriteRequest(path, content))
    }
}

class PcGitCommitUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(message: String, files: List<String> = emptyList()): Result<String> {
        return repository.gitCommit(PcGitCommitRequest(message, files))
    }
}

class PcShellExecuteUseCase @Inject constructor(
    private val repository: PcConnectionRepository
) {
    suspend operator fun invoke(command: String, cwd: String? = null): Result<String> {
        return repository.executeShell(PcShellRequest(command, cwd))
    }
}
