package com.portfolio.pingme.ui.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.portfolio.pingme.ui.theme.*

@Composable
fun StatusScreen(viewModel: ChatViewModel) {
    var showAddStatusDialog by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }
    var isVoiceStatus by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppLightBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Updates",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = WhatsAppTextDark
                )
            }

            // My Status Item
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(56.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(WhatsAppDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Me", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(WhatsAppDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = WhatsAppTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap to add text or voice status",
                            fontSize = 13.sp,
                            color = WhatsAppTextMuted
                        )
                    }

                    Button(
                        onClick = { showAddStatusDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Add Status")
                    }
                }
            }

            item {
                Text(
                    text = "Recent updates",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = WhatsAppTextMuted
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "WhatsApp Status Feature Active",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = WhatsAppTextDark
                        )
                        Text(
                            text = "Share text and voice notes as your 24-hour status update with contacts.",
                            fontSize = 13.sp,
                            color = WhatsAppTextMuted
                        )
                    }
                }
            }
        }

        // Add Status Dialog
        if (showAddStatusDialog) {
            AlertDialog(
                onDismissRequest = { showAddStatusDialog = false },
                title = { Text(if (isVoiceStatus) "Record Voice Status" else "Post Text Status") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (!isVoiceStatus) {
                            OutlinedTextField(
                                value = statusText,
                                onValueChange = { statusText = it },
                                placeholder = { Text("Type your status here...") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(WhatsAppLightBg, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Mic", tint = Color.Red)
                                    Text("Voice status recording active...", color = WhatsAppTextDark)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { isVoiceStatus = !isVoiceStatus }) {
                                Text(if (isVoiceStatus) "Switch to Text" else "Switch to Voice")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val firestore = FirebaseFirestore.getInstance()
                            val statusPayload = hashMapOf(
                                "userId" to viewModel.currentUserId,
                                "type" to if (isVoiceStatus) "VOICE" else "TEXT",
                                "content" to if (isVoiceStatus) "🎙️ Voice Status Update" else statusText,
                                "timestamp" to System.currentTimeMillis()
                            )
                            firestore.collection("statuses").add(statusPayload)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Status posted successfully!", Toast.LENGTH_SHORT).show()
                                    showAddStatusDialog = false
                                    statusText = ""
                                }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen)
                    ) {
                        Text("Post")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStatusDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
