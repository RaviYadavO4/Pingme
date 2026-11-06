package com.portfolio.pingme.domain.repository

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthResult>
    fun sendOTP(phoneNumber: String, activity: Activity)
    fun verifyOTP(verificationId: String, otp: String)
}
