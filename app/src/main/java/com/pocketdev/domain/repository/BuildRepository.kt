package com.pocketdev.domain.repository

import com.pocketdev.domain.model.BuildConfig
import com.pocketdev.domain.model.BuildProgress
import com.pocketdev.domain.model.BuildResult
import com.pocketdev.domain.model.GradleInfo
import kotlinx.coroutines.flow.Flow

interface BuildRepository {
    suspend fun getGradleInfo(projectPath: String): Result<GradleInfo>
    fun executeBuild(config: BuildConfig): Flow<BuildProgress>
    suspend fun cancelBuild(buildId: String): Result<Unit>
    fun getBuildHistory(): Flow<List<BuildResult>>
}