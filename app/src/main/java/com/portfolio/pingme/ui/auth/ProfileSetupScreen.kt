package com.portfolio.pingme.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.ui.chat.ChatViewModel
import com.portfolio.pingme.ui.theme.WhatsAppDarkGreen
import com.portfolio.pingme.ui.theme.WhatsAppLightBg
import com.portfolio.pingme.ui.theme.WhatsAppTextMuted

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
            .background(WhatsAppLightBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Profile Info",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhatsAppDarkGreen
                )

                Text(
                    text = "Please provide your name and optional profile name for your contacts.",
                    fontSize = 14.sp,
                    color = WhatsAppTextMuted
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Name") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (name.trim().isNotEmpty()) {
                            isLoading = true

                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                val deviceToken = if (task.isSuccessful) task.result else ""

                                val currentUser = firebaseAuth.currentUser
                                val user = User(
                                    uid = currentUser?.uid ?: "",
                                    name = name.trim(),
                                    phoneNumber = currentUser?.phoneNumber ?: "",
                                    profileImageUrl = "",
                                    isOnline = true,
                                    fcmToken = deviceToken
                                )

                                val firestore = FirebaseFirestore.getInstance()
                                firestore.collection("users").document(user.uid).set(user)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        onSaveSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            e.localizedMessage ?: "Error saving profile",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Save & Continue", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
