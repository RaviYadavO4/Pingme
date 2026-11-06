package com.portfolio.pingme.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.portfolio.pingme.domain.model.Message
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.domain.repository.ChatRepository
import com.portfolio.pingme.domain.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // Current logged-in user id
    val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    // State for Users List
    private val _usersState = MutableStateFlow<Resource<List<User>>>(Resource.Loading)
    val usersState: StateFlow<Resource<List<User>>> = _usersState.asStateFlow()

    // State for Messages list inside a chat room
    private val _messagesState = MutableStateFlow<List<Message>>(emptyList())
    val messagesState: StateFlow<List<Message>> = _messagesState.asStateFlow()

    init {
        fetchAllUsers()
    }

    // 1. Fetch all registered users for contact listing
    fun fetchAllUsers() {
        viewModelScope.launch {
            chatRepository.getAllUsers().collectLatest { resource ->
                // Filter out the currently logged-in user so they don't see themselves in the list
                if (resource is Resource.Success) {
                    val filteredList = resource.data.filter { it.uid != currentUserId }
                    _usersState.value = Resource.Success(filteredList)
                } else {
                    _usersState.value = resource
                }
            }
        }
    }

    // 2. Listen to real-time messages in a specific room
    fun listenToMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId).collectLatest { messages ->
                _messagesState.value = messages
            }
        }
    }

    // 3. Send text message pipeline
    fun sendTextMessage(receiverId: String, textMessage: String) {
        if (textMessage.trim().isEmpty()) return

        viewModelScope.launch {
            val chatRoomId = generateChatRoomId(currentUserId, receiverId)
            val message = Message(
                senderId = currentUserId,
                receiverId = receiverId,
                text = textMessage.trim(),
                timestamp = System.currentTimeMillis(),
                messageType = "TEXT"
            )
            chatRepository.sendMessage(chatRoomId, message)
        }
    }

    // Helper function to create a unique and deterministic room ID between two users
    fun generateChatRoomId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_$uid2" else "${uid2}_$uid1"
    }
}
