package com.portfolio.pingme.util

import android.content.Context
import android.util.Log
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

object CallManager {

    private var rtcEngine: RtcEngine? = null

    // REPLACE WITH YOUR RECENTLY ACTIVATED AGORA PROD TOKEN/ID VALUE
    private const val APP_ID = "0c490ac36a0e4d7ba7b9c49ad5352a9d"

    fun initAgoraEngine(context: Context) {
        if (rtcEngine != null) return
        try {
            val config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = APP_ID
                mEventHandler = object : IRtcEngineEventHandler() {
                    override fun onUserJoined(uid: Int, elapsed: Int) {
                        Log.d("AGORA_CALL", "Remote user joined pipeline: $uid")
                    }

                    override fun onUserOffline(uid: Int, reason: Int) {
                        Log.d("AGORA_CALL", "Remote user offline: $uid")
                    }
                }
            }
            rtcEngine = RtcEngine.create(config)
            rtcEngine?.enableVideo()
        } catch (e: Exception) {
            Log.e("AGORA_CALL", "Hardware pipeline failed to attach: ${e.localizedMessage}")
        }
    }

    fun joinCallChannel(channelId: String) {
        rtcEngine?.joinChannel(null, channelId, "", 0)
        rtcEngine?.startPreview()
    }

    fun leaveCallChannel() {
        rtcEngine?.leaveChannel()
    }
}
