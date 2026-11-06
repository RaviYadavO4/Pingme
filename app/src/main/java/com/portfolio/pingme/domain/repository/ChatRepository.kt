package com.portfolio.pingme.domain.repository

import com.portfolio.pingme.domain.model.Message
import com.portfolio.pingme.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun saveUserToFirestore(user: User): Flow<Resource<Boolean>>
    fun getAllUsers(): Flow<Resource<List<User>>>
    fun getMessages(chatRoomId: String): Flow<List<Message>>
    suspend fun sendMessage(chatRoomId: String, message: Message): Resource<Boolean>
}

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
