package com.pocketdev.domain.repository

import com.pocketdev.domain.model.PcConnectionConfig
import com.pocketdev.domain.model.PcFileReadRequest
import com.pocketdev.domain.model.PcFileWriteRequest
import com.pocketdev.domain.model.PcGitCommitRequest
import com.pocketdev.domain.model.PcShellRequest
import kotlinx.coroutines.flow.Flow

interface PcConnectionRepository {
    val connectionsFlow: Flow<List<PcConnectionConfig>>
    val activeConnectionFlow: Flow<PcConnectionConfig?>

    suspend fun getConnections(): List<PcConnectionConfig>
    suspend fun addConnection(config: PcConnectionConfig): Result<Unit>
    suspend fun removeConnection(id: String): Result<Unit>
    suspend fun setActiveConnection(id: String): Result<Unit>
    suspend fun testConnection(id: String): Result<Boolean>

    suspend fun readFile(request: PcFileReadRequest): Result<String>
    suspend fun writeFile(request: PcFileWriteRequest): Result<Unit>
    suspend fun gitCommit(request: PcGitCommitRequest): Result<String>
    suspend fun executeShell(request: PcShellRequest): Result<String>
}
