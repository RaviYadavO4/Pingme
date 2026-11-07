package com.portfolio.pingme.ui.chat

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.pingme.domain.model.Message
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.util.CallManager
import com.portfolio.pingme.util.ImageUtil
import com.portfolio.pingme.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    receiverUser: User,
    onBackClick: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messagesState.collectAsState()
    val chatRoomId = viewModel.generateChatRoomId(viewModel.currentUserId, receiverUser.uid)
    val context = LocalContext.current
    var isUserInCall by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.sendImageMessage(context, receiverUser.uid, it) }
    }

    LaunchedEffect(chatRoomId) {
        viewModel.listenToMessages(chatRoomId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (receiverUser.name.isEmpty()) receiverUser.phoneNumber else receiverUser.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (isUserInCall) "Active Agora connection layer..." else "Tap tools to call",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        CallManager.joinCallChannel(chatRoomId)
                        viewModel.triggerOutgoingCall(receiverUser.uid, chatRoomId)
                        isUserInCall = true
                    }) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Audio Call", tint = Color.White)
                    }
                    IconButton(onClick = {
                        CallManager.joinCallChannel(chatRoomId)
                        viewModel.triggerOutgoingCall(receiverUser.uid, chatRoomId)
                        isUserInCall = true
                    }) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Video Call", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleMain)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(PurpleLightBg)
        ) {
            if (isUserInCall) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dialing remote streams...", fontWeight = FontWeight.Bold, color = Color.Red)
                        Button(
                            onClick = {
                                CallManager.leaveCallChannel()
                                viewModel.endCallSession(receiverUser.uid)
                                isUserInCall = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("End Call", color = Color.White)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    val isMe = message.senderId == viewModel.currentUserId
                    PremiumMessageBubble(message = message, isMe = isMe)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Attach", tint = PurpleMain)
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { newValue: String -> messageText = newValue },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Write a message...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PurpleMain.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        if (messageText.trim().isNotEmpty()) {
                            viewModel.sendTextMessage(receiverUser.uid, messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(PurpleMain)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun PremiumMessageBubble(message: Message, isMe: Boolean) {
    val bubbleColor = if (isMe) PurpleBubbleMe else PurpleBubbleThem
    val textColor = if (isMe) Color.White else TextDark
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeText = timeFormat.format(Date(message.timestamp))

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(
            modifier = Modifier
                .widthIn(max = 270.dp)
                .clip(shape)
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (message.messageType == "IMAGE") {
                val bitmap = remember(message.text) { ImageUtil.base64ToBitmap(message.text) }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Shared Media",
                        modifier = Modifier.width(250.dp).height(180.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Text(text = message.text, fontSize = 15.sp, color = textColor)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = timeText,
                fontSize = 10.sp,
                color = if (isMe) Color.White.copy(alpha = 0.7f) else TextMuted,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}