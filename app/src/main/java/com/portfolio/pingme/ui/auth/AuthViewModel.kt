package com.portfolio.pingme.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.pingme.domain.repository.AuthRepository
import com.portfolio.pingme.domain.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthResult> = authRepository.authState

    fun sendOtp(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            authRepository.sendOTP(phoneNumber, activity)
        }
    }

    fun verifyOtp(verificationId: String, otp: String) {
        viewModelScope.launch {
            authRepository.verifyOTP(verificationId, otp)
        }
    }
}
