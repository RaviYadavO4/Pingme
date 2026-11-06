package com.portfolio.pingme.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.domain.repository.Resource
import com.portfolio.pingme.ui.chat.ChatViewModel

@Composable
fun ProfileSetupScreen(
    chatViewModel: ChatViewModel,
    firebaseAuth: FirebaseAuth,
    onSaveSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Profile Info",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Please provide your name to complete registration.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = name,
                onValueChange = { newValue: String -> name = newValue },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your Name") },
                singleLine = true
            )

            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        isLoading = true
                        val currentUser = firebaseAuth.currentUser
                        val user = User(
                            uid = currentUser?.uid ?: "",
                            name = name.trim(),
                            phoneNumber = currentUser?.phoneNumber ?: "",
                            isOnline = true
                        )
                        // Trigger saving via safe stream tracking
                        chatViewModel.sendTextMessage(user.uid, "") // Just to establish session if needed

                        // Directly writing the mapping layout
                        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        firestore.collection("users").document(user.uid).set(user)
                            .addOnSuccessListener {
                                isLoading = false
                                onSaveSuccess()
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, "Error saving profile", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save & Continue", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
