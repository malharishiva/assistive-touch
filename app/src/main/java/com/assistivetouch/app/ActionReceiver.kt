package com.assistivetouch.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_VOL_UP = "com.assistivetouch.ACTION_VOL_UP"
        const val ACTION_VOL_DOWN = "com.assistivetouch.ACTION_VOL_DOWN"
        const val ACTION_LOCK_SCREEN = "com.assistivetouch.ACTION_LOCK_SCREEN"
        const val ACTION_POWER_MENU = "com.assistivetouch.ACTION_POWER_MENU"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            ACTION_VOL_UP -> ActionHelper.volumeUp(context)
            ACTION_VOL_DOWN -> ActionHelper.volumeDown(context)
            ACTION_LOCK_SCREEN -> ActionHelper.lockScreen(context)
            ACTION_POWER_MENU -> ActionHelper.openPowerMenu(context)
        }
    }
}
