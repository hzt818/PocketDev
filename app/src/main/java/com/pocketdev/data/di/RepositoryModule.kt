package com.pocketdev.data.di

import com.pocketdev.data.repository.AiRepositoryImpl
import com.pocketdev.data.repository.CollaborationRepositoryImpl
import com.pocketdev.data.repository.ConversationRepositoryImpl
import com.pocketdev.data.repository.GitHubRepositoryImpl
import com.pocketdev.data.repository.LlmRepositoryImpl
import com.pocketdev.data.repository.OllamaRepositoryImpl
import com.pocketdev.data.repository.PcConnectionRepositoryImpl
import com.pocketdev.data.repository.RemoteRepositoryImpl
import com.pocketdev.data.repository.TerminalRepositoryImpl
import com.pocketdev.data.repository.UserSettingsRepositoryImpl
import com.pocketdev.domain.repository.AiRepository
import com.pocketdev.domain.repository.CollaborationRepository
import com.pocketdev.domain.repository.ConversationRepository
import com.pocketdev.domain.repository.GitHubRepository
import com.pocketdev.domain.repository.LlmRepository
import com.pocketdev.domain.repository.OllamaRepository
import com.pocketdev.domain.repository.PcConnectionRepository
import com.pocketdev.domain.repository.RemoteRepositoryGateway
import com.pocketdev.domain.repository.TerminalRepository
import com.pocketdev.domain.repository.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(
        impl: UserSettingsRepositoryImpl
    ): UserSettingsRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        impl: ConversationRepositoryImpl
    ): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        impl: AiRepositoryImpl
    ): AiRepository

    @Binds
    @Singleton
    abstract fun bindLlmRepository(
        impl: LlmRepositoryImpl
    ): LlmRepository

    @Binds
    @Singleton
    abstract fun bindGitHubRepository(
        impl: GitHubRepositoryImpl
    ): GitHubRepository

    @Binds
    @Singleton
    abstract fun bindOllamaRepository(
        impl: OllamaRepositoryImpl
    ): OllamaRepository

    @Binds
    @Singleton
    abstract fun bindPcConnectionRepository(
        impl: PcConnectionRepositoryImpl
    ): PcConnectionRepository

    @Binds
    @Singleton
    abstract fun bindRemoteRepositoryGateway(
        impl: RemoteRepositoryImpl
    ): RemoteRepositoryGateway

    @Binds
    @Singleton
    abstract fun bindTerminalRepository(
        impl: TerminalRepositoryImpl
    ): TerminalRepository

    @Binds
    @Singleton
    abstract fun bindCollaborationRepository(
        impl: CollaborationRepositoryImpl
    ): CollaborationRepository
}