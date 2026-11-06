package com.portfolio.pingme.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.portfolio.pingme.domain.model.Message
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.domain.repository.ChatRepository
import com.portfolio.pingme.domain.repository.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun saveUserToFirestore(user: User): Flow<Resource<Boolean>> = callbackFlow {
        trySend(Resource.Loading)
        firestore.collection("users").document(user.uid).set(user)
            .addOnSuccessListener {
                trySend(Resource.Success(true))
            }
            .addOnFailureListener { e ->
                trySend(Resource.Error(e.localizedMessage ?: "Failed to save user"))
            }
        awaitClose()
    }

    override fun getAllUsers(): Flow<Resource<List<User>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Failed to fetch users"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val usersList = snapshot.toObjects(User::class.java)
                    trySend(Resource.Success(usersList))
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getMessages(chatRoomId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("chat_rooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val messagesList = snapshot.toObjects(Message::class.java)
                    trySend(messagesList)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(chatRoomId: String, message: Message): Resource<Boolean> {
        return try {
            val messageId = firestore.collection("chat_rooms")
                .document(chatRoomId)
                .collection("messages")
                .document().id

            val finalMessage = message.copy(messageId = messageId)

            firestore.collection("chat_rooms")
                .document(chatRoomId)
                .collection("messages")
                .document(messageId)
                .set(finalMessage)
                .await()

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to send message")
        }
    }
}
