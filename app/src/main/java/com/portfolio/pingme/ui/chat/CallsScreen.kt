package com.portfolio.pingme.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.domain.repository.Resource
import com.portfolio.pingme.ui.theme.*
import com.portfolio.pingme.util.CallManager

@Composable
fun CallsScreen(
    viewModel: ChatViewModel,
    onStartCall: (User, Boolean) -> Unit // (user, isVideo)
) {
    val usersState by viewModel.usersState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppLightBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = WhatsAppTextDark
                    )
                }
            }

            item {
                Text(
                    text = "Start a WhatsApp-style Call",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = WhatsAppTextMuted
                )
            }

            when (usersState) {
                is Resource.Success -> {
                    val users = (usersState as Resource.Success<List<User>>).data
                    items(users) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(WhatsAppLightBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (user.name.isNotEmpty()) user.name.take(1).uppercase() else "?",
                                        fontWeight = FontWeight.Bold,
                                        color = WhatsAppDarkGreen
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (user.name.isEmpty()) user.phoneNumber else user.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = WhatsAppTextDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Tap icon to call",
                                        fontSize = 12.sp,
                                        color = WhatsAppTextMuted
                                    )
                                }

                                // Audio Call Button
                                IconButton(
                                    onClick = {
                                        val roomId = viewModel.generateChatRoomId(viewModel.currentUserId, user.uid)
                                        CallManager.joinCallChannel(roomId)
                                        viewModel.triggerOutgoingCall(user.uid, roomId)
                                        onStartCall(user, false)
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = "Voice Call", tint = WhatsAppDarkGreen)
                                }

                                // Video Call Button
                                IconButton(
                                    onClick = {
                                        val roomId = viewModel.generateChatRoomId(viewModel.currentUserId, user.uid)
                                        CallManager.joinCallChannel(roomId)
                                        viewModel.triggerOutgoingCall(user.uid, roomId)
                                        onStartCall(user, true)
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = WhatsAppDarkGreen)
                                }
                            }
                        }
                    }
                }
                else -> {
                    item {
                        CircularProgressIndicator(color = WhatsAppDarkGreen)
                    }
                }
            }
        }
    }
}
