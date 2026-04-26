package com.pocketdev.data.di

import com.pocketdev.data.repository.FileRepositoryImpl
import com.pocketdev.domain.repository.FileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileModule {

    @Binds
    @Singleton
    abstract fun bindFileRepository(
        impl: FileRepositoryImpl
    ): FileRepository
}
