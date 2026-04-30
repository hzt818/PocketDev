package com.pocketdev.data.di

import com.pocketdev.data.build.GradleExecutor
import com.pocketdev.data.repository.BuildRepositoryImpl
import com.pocketdev.domain.repository.BuildRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BuildModule {

    @Binds
    @Singleton
    abstract fun bindBuildRepository(
        impl: BuildRepositoryImpl
    ): BuildRepository

    companion object {
        @Provides
        @Singleton
        fun provideGradleExecutor(): GradleExecutor {
            return GradleExecutor()
        }
    }
}