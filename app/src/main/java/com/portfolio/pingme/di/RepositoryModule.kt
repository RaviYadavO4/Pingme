package com.portfolio.pingme.di

import com.portfolio.pingme.data.repository.AuthRepositoryImpl
import com.portfolio.pingme.data.repository.ChatRepositoryImpl
import com.portfolio.pingme.domain.repository.AuthRepository
import com.portfolio.pingme.domain.repository.ChatRepository
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
}
