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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.portfolio.pingme.domain.model.User
import com.portfolio.pingme.ui.auth.AuthViewModel
import com.portfolio.pingme.ui.auth.LoginScreen
import com.portfolio.pingme.ui.auth.OtpScreen
import com.portfolio.pingme.ui.auth.ProfileSetupScreen
import com.portfolio.pingme.ui.chat.ChatScreen
import com.portfolio.pingme.ui.chat.ChatViewModel
import com.portfolio.pingme.ui.chat.UsersListScreen
import com.portfolio.pingme.ui.theme.PingMeTheme
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

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                }
            }
        }
    }
}
