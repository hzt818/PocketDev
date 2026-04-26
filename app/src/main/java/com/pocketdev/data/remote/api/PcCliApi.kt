package com.pocketdev.data.remote.api

import com.pocketdev.domain.model.PcFileReadRequest
import com.pocketdev.domain.model.PcFileWriteRequest
import com.pocketdev.domain.model.PcFileResponse
import com.pocketdev.domain.model.PcGitCommitRequest
import com.pocketdev.domain.model.PcGitCommitResponse
import com.pocketdev.domain.model.PcShellRequest
import com.pocketdev.domain.model.PcShellResponse
import com.pocketdev.domain.model.PcSystemInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PcCliApi {
    @GET("api/system/info")
    suspend fun getSystemInfo(): PcSystemInfo

    @POST("api/files/read")
    suspend fun readFile(@Body request: PcFileReadRequest): PcFileResponse

    @POST("api/files/write")
    suspend fun writeFile(@Body request: PcFileWriteRequest): PcFileResponse

    @POST("api/git/commit")
    suspend fun gitCommit(@Body request: PcGitCommitRequest): PcGitCommitResponse

    @POST("api/shell/execute")
    suspend fun executeShell(@Body request: PcShellRequest): PcShellResponse
}
