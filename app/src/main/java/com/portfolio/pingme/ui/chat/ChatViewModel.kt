package com.portfolio.pingme.ui.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.portfolio.pingme.domain.model.Message
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.domain.repository.ChatRepository
import com.portfolio.pingme.domain.repository.Resource
import com.portfolio.pingme.util.ImageUtil
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

    val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    private val _usersState = MutableStateFlow<Resource<List<User>>>(Resource.Loading)
    val usersState: StateFlow<Resource<List<User>>> = _usersState.asStateFlow()

    private val _messagesState = MutableStateFlow<List<Message>>(emptyList())
    val messagesState: StateFlow<List<Message>> = _messagesState.asStateFlow()

    init {
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            chatRepository.getAllUsers().collectLatest { resource ->
                if (resource is Resource.Success) {
                    val filteredList = resource.data.filter { it.uid != currentUserId }
                    _usersState.value = Resource.Success(filteredList)
                } else {
                    _usersState.value = resource
                }
            }
        }
    }

    fun listenToMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId).collectLatest { messages ->
                _messagesState.value = messages
            }
        }
    }

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

    // ==========================================
    // NEW: PHASE 3 MEDIA IMAGE SENDING PIPELINE
    // ==========================================
    fun sendImageMessage(context: Context, receiverId: String, imageUri: Uri) {
        viewModelScope.launch {
            // 1. Convert Image Uri to Compressed Base64 String
            val base64Image = ImageUtil.uriToBase64(context, imageUri)

            if (base64Image != null) {
                val chatRoomId = generateChatRoomId(currentUserId, receiverId)

                // 2. Build Message with type "IMAGE"
                val message = Message(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    text = base64Image, // Base64 Text context is injected as payload
                    timestamp = System.currentTimeMillis(),
                    messageType = "IMAGE"
                )

                // 3. Dispatch to Firestore pipeline
                chatRepository.sendMessage(chatRoomId, message)
            }
        }
    }

    fun generateChatRoomId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_$uid2" else "${uid2}_$uid1"
    }
}
