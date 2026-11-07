package com.portfolio.pingme

import android.app.Application
import com.portfolio.pingme.util.CallManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PingMeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Agora SDK once app opens
        CallManager.initAgoraEngine(this)
    }
}
