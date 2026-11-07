package com.portfolio.pingme.util

import android.content.Context
import android.util.Log
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

object CallManager {

    private var rtcEngine: RtcEngine? = null
    private const val APP_ID = "YOUR_AGORA_APP_ID_HERE" // Paste Agora App ID here

    // Initialize Agora Engine Engine
    fun initAgoraEngine(context: Context) {
        if (rtcEngine != null) return
        try {
            val config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = APP_ID
                mEventHandler = object : IRtcEngineEventHandler() {
                    override fun onUserJoined(uid: Int, elapsed: Int) {
                        Log.d("AGORA_CALL", "Remote user joined channel: $uid")
                    }

                    override fun onUserOffline(uid: Int, reason: Int) {
                        Log.d("AGORA_CALL", "Remote user went offline: $uid")
                    }
                }
            }
            rtcEngine = RtcEngine.create(config)

            // Enable video layout configurations
            rtcEngine?.enableVideo()
        } catch (e: Exception) {
            Log.e("AGORA_CALL", "Initialization failed: ${e.localizedMessage}")
        }
    }

    // Join a dynamic Voice/Video room channel
    fun joinCallChannel(channelId: String) {
        rtcEngine?.joinChannel(null, channelId, "", 0)
        rtcEngine?.startPreview()
    }

    // Leave call room
    fun leaveCallChannel() {
        rtcEngine?.leaveChannel()
    }
}
