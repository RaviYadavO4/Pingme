package com.portfolio.pingme.ui.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.pingme.domain.repository.AuthResult

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onCodeSent: (String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }
    val context = LocalContext.current
    val activity = context as Activity
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.CodeSent -> {
                val verificationId = (authState as AuthResult.CodeSent).verificationId
                onCodeSent(verificationId)
            }
            is AuthResult.Success -> {
                onLoginSuccess()
            }
            is AuthResult.Error -> {
                val errorMsg = (authState as AuthResult.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Enter your phone number",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "PingMe will need to verify your phone number to set up your profile.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = countryCode,
                    onValueChange = { newValue: String -> countryCode = newValue },
                    modifier = Modifier.width(90.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { newValue: String -> phoneNumber = newValue },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (phoneNumber.trim().length >= 10) {
                        val fullNumber = "$countryCode${phoneNumber.trim()}"
                        viewModel.sendOtp(fullNumber, activity)
                    } else {
                        Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = authState !is AuthResult.Loading
            ) {
                if (authState is AuthResult.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
