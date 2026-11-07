package com.portfolio.pingme.ui.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    // Calling Live Stream Hooks
    val incomingCallChannel = MutableStateFlow<String?>(null)
    val callerNameState = MutableStateFlow<String>("")

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

            // 🚀 SMART FREE BYPASS: Inject immediate cross-device push alert payload ticket
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(currentUserId).get().addOnSuccessListener { senderDoc ->
                val senderName = senderDoc.getString("name") ?: "New Message"

                val notificationPayload = hashMapOf(
                    "receiverId" to receiverId,
                    "title" to senderName,
                    "body" to textMessage.trim(),
                    "timestamp" to System.currentTimeMillis()
                )
                firestore.collection("notifications").document().set(notificationPayload)
            }
        }
    }

    fun sendImageMessage(context: Context, receiverId: String, imageUri: Uri) {
        viewModelScope.launch {
            val base64Image = ImageUtil.uriToBase64(context, imageUri)
            if (base64Image != null) {
                val chatRoomId = generateChatRoomId(currentUserId, receiverId)
                val message = Message(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    text = base64Image,
                    timestamp = System.currentTimeMillis(),
                    messageType = "IMAGE"
                )
                chatRepository.sendMessage(chatRoomId, message)

                // Trigger simulated popup notification payload for image sharing as well
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users").document(currentUserId).get().addOnSuccessListener { senderDoc ->
                    val senderName = senderDoc.getString("name") ?: "New Message"
                    val notificationPayload = hashMapOf(
                        "receiverId" to receiverId,
                        "title" to senderName,
                        "body" to "📷 Shared a photo",
                        "timestamp" to System.currentTimeMillis()
                    )
                    firestore.collection("notifications").document().set(notificationPayload)
                }
            }
        }
    }

    // Call Handlers Loop Framework
    fun listenForIncomingCalls() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("calls").document(currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    val status = snapshot.getString("status") ?: ""
                    if (status == "RINGING") {
                        incomingCallChannel.value = snapshot.getString("channelId")
                        callerNameState.value = snapshot.getString("callerName") ?: "PingMe Caller"
                    } else {
                        incomingCallChannel.value = null
                    }
                } else {
                    incomingCallChannel.value = null
                }
            }
    }

    fun triggerOutgoingCall(receiverId: String, channelId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(currentUserId).get().addOnSuccessListener { senderDoc ->
            val senderName = senderDoc.getString("name") ?: "PingMe User"
            val callPayload = hashMapOf(
                "callerId" to currentUserId,
                "callerName" to senderName,
                "channelId" to channelId,
                "status" to "RINGING"
            )
            firestore.collection("calls").document(receiverId).set(callPayload)
        }
    }

    fun endCallSession(targetUserId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("calls").document(targetUserId).delete()
        firestore.collection("calls").document(currentUserId).delete()
    }

    fun generateChatRoomId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_$uid2" else "${uid2}_$uid1"
    }
}