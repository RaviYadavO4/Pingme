package com.portfolio.pingme.domain.repository

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class CodeSent(val verificationId: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
