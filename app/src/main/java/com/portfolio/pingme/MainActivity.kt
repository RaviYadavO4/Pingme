package com.portfolio.pingme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.portfolio.pingme.ui.auth.AuthViewModel
import com.portfolio.pingme.ui.auth.LoginScreen
import com.portfolio.pingme.ui.auth.OtpScreen
import com.portfolio.pingme.ui.theme.PingMeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PingMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Simple Navigation State Management
                    var currentScreen by remember { mutableStateOf("login") }
                    var verificationId by remember { mutableStateOf("") }

                    // Check if user is already logged in
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
                                    currentScreen = "home"
                                }
                            )
                        }
                        "otp" -> {
                            OtpScreen(
                                viewModel = authViewModel,
                                verificationId = verificationId,
                                onVerificationSuccess = {
                                    currentScreen = "home"
                                }
                            )
                        }
                        "home" -> {
                            // Temporary placeholder for successful login.
                            // Phase 2 mein yahan hamari ChatListScreen aayegi.
                            Toast.makeText(this@MainActivity, "Welcome to PingMe!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
