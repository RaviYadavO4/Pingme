package com.portfolio.pingme.util

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object NotificationSender {

    private const val FCM_URL = "https://googleapis.com"
    private const val SERVER_KEY = "YOUR_SERVER_KEY_HERE" // Paste your Step 1 server key here

    fun sendChatNotification(receiverToken: String, senderName: String, messageText: String) {
        if (receiverToken.isEmpty()) return

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()

        // Construct the exact JSON Payload Firebase Server expects
        val jsonBody = JSONObject().apply {
            put("to", receiverToken)
            put("notification", JSONObject().apply {
                put("title", senderName)
                put("body", messageText)
                put("sound", "default")
            })
            put("data", JSONObject().apply {
                put("click_action", "FLUTTER_NOTIFICATION_CLICK") // Triggers system open activity
            })
        }

        val request = Request.Builder()
            .url(FCM_URL)
            .post(jsonBody.toString().toRequestBody(mediaType))
            .addHeader("Authorization", "key=$SERVER_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM_SEND", "Notification failed to dispatch", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("FCM_SEND", "Notification successfully dispatched: ${response.body?.string()}")
            }
        })
    }
}
