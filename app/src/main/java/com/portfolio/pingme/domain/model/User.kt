package com.portfolio.pingme.domain.model

data class User(
    val uid: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val isOnline: Boolean = false,
    val fcmToken: String = "" // NEW: Device Token Storage
)
