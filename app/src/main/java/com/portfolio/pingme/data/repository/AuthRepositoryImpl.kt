package com.portfolio.pingme.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.portfolio.pingme.domain.repository.AuthRepository
import com.portfolio.pingme.domain.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    override val authState: StateFlow<AuthResult> = _authState.asStateFlow()

    private var storedVerificationId: String? = null

    override fun sendOTP(phoneNumber: String, activity: Activity) {
        _authState.value = AuthResult.Loading

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification ya instant validation ke liye
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _authState.value = AuthResult.Error(e.localizedMessage ?: "Verification Failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    storedVerificationId = verificationId
                    _authState.value = AuthResult.CodeSent(verificationId)
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyOTP(verificationId: String, otp: String) {
        _authState.value = AuthResult.Loading
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthResult.Success
                } else {
                    _authState.value = AuthResult.Error(task.exception?.localizedMessage ?: "Invalid OTP")
                }
            }
    }
}
