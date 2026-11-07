package com.portfolio.pingme.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.pingme.domain.repository.AuthResult
import com.portfolio.pingme.ui.theme.WhatsAppDarkGreen
import com.portfolio.pingme.ui.theme.WhatsAppLightBg
import com.portfolio.pingme.ui.theme.WhatsAppTextMuted

@Composable
fun OtpScreen(
    viewModel: AuthViewModel,
    verificationId: String,
    onVerificationSuccess: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            onVerificationSuccess()
        } else if (authState is AuthResult.Error) {
            val errorMsg = (authState as AuthResult.Error).message
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

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
                    text = "Verifying your number",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhatsAppDarkGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Waiting to automatically detect an SMS sent to your number. Enter the 6-digit code below.",
                    fontSize = 14.sp,
                    color = WhatsAppTextMuted,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { newValue ->
                        if (newValue.length <= 6) otpCode = newValue
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    label = { Text("6-Digit Code") },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (otpCode.trim().length == 6) {
                            viewModel.verifyOtp(verificationId, otpCode.trim())
                        } else {
                            Toast.makeText(context, "Please enter a valid 6-digit code", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen),
                    enabled = authState !is AuthResult.Loading
                ) {
                    if (authState is AuthResult.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
