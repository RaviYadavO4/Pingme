package com.portfolio.pingme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.ui.auth.AuthViewModel
import com.portfolio.pingme.ui.auth.LoginScreen
import com.portfolio.pingme.ui.auth.OtpScreen
import com.portfolio.pingme.ui.auth.ProfileSetupScreen
import com.portfolio.pingme.ui.chat.ChatScreen
import com.portfolio.pingme.ui.chat.ChatViewModel
import com.portfolio.pingme.ui.chat.UsersListScreen
import com.portfolio.pingme.ui.theme.PingMeTheme
import com.portfolio.pingme.util.CallManager
import com.portfolio.pingme.util.NotificationUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val authViewModel: AuthViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PingMeTheme {
                val context = LocalContext.current

                // 1. Notification Permission Launcher for Android 13+
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Permission status captured (true/false)
                }

                // 2. Request permission automatically if on API 33+
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // 3. Global Background Listeners (Incoming Calls & Chat Notifications across ALL screens)
                val incomingChannel by chatViewModel.incomingCallChannel.collectAsState()
                val callerName by chatViewModel.callerNameState.collectAsState()
                var isCallConnected by remember { mutableStateOf(false) }

                LaunchedEffect(chatViewModel.currentUserId) {
                    if (chatViewModel.currentUserId.isNotEmpty()) {
                        chatViewModel.listenForIncomingCalls()

                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("notifications")
                            .whereEqualTo("receiverId", chatViewModel.currentUserId)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) return@addSnapshotListener
                                if (snapshot != null && !snapshot.isEmpty) {
                                    for (doc in snapshot.documentChanges) {
                                        if (doc.type == DocumentChange.Type.ADDED) {
                                            val title = doc.document.getString("title") ?: "PingMe Message"
                                            val body = doc.document.getString("body") ?: "You have a new message"

                                            // Trigger system notification globally
                                            NotificationUtil.showNotification(context, title, body)

                                            // Consume/delete temp notification ticket
                                            firestore.collection("notifications").document(doc.document.id).delete()
                                        }
                                    }
                                }
                            }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var currentScreen by remember { mutableStateOf("login") }
                        var verificationId by remember { mutableStateOf("") }
                        var selectedReceiver by remember { mutableStateOf<User?>(null) }

                        LaunchedEffect(Unit) {
                            if (firebaseAuth.currentUser != null) {
                                currentScreen = "home"
                            }
                        }

                        when (currentScreen) {
                            "login" -> {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onCodeSent = { vId ->
                                        verificationId = vId
                                        currentScreen = "otp"
                                    },
                                    onLoginSuccess = {
                                        currentScreen = "profile_setup"
                                    }
                                )
                            }
                            "otp" -> {
                                OtpScreen(
                                    viewModel = authViewModel,
                                    verificationId = verificationId,
                                    onVerificationSuccess = {
                                        currentScreen = "profile_setup"
                                    }
                                )
                            }
                            "profile_setup" -> {
                                ProfileSetupScreen(
                                    chatViewModel = chatViewModel,
                                    firebaseAuth = firebaseAuth,
                                    onSaveSuccess = {
                                        chatViewModel.fetchAllUsers()
                                        currentScreen = "home"
                                    }
                                )
                            }
                            "home" -> {
                                UsersListScreen(
                                    viewModel = chatViewModel,
                                    onUserClick = { user ->
                                        selectedReceiver = user
                                        currentScreen = "chat_room"
                                    }
                                )
                            }
                            "chat_room" -> {
                                selectedReceiver?.let { receiver ->
                                    ChatScreen(
                                        viewModel = chatViewModel,
                                        receiverUser = receiver,
                                        onBackClick = {
                                            currentScreen = "home"
                                        }
                                    )
                                }
                            }
                        }

                        // 4. Global Incoming Call Overlay HUD (Appears anywhere in app when someone calls)
                        if (incomingChannel != null && !isCallConnected) {
                            AlertDialog(
                                onDismissRequest = { },
                                title = { Text("🚨 Incoming Call", fontWeight = FontWeight.Bold) },
                                text = { Text("$callerName is calling you via high-quality Agora channel.") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            CallManager.joinCallChannel(incomingChannel!!)
                                            isCallConnected = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EC4B6))
                                    ) {
                                        Text("Accept Call", color = Color.White)
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { chatViewModel.endCallSession(chatViewModel.currentUserId) }
                                    ) {
                                        Text("Decline", color = Color.Red)
                                    }
                                }
                            )
                        }

                        // Active Call Disconnect Overlay
                        if (isCallConnected) {
                            AlertDialog(
                                onDismissRequest = { },
                                title = { Text("Call Connected") },
                                text = { Text("Secure voice/video session active with $callerName") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            CallManager.leaveCallChannel()
                                            chatViewModel.endCallSession(chatViewModel.currentUserId)
                                            isCallConnected = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Text("Disconnect")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
