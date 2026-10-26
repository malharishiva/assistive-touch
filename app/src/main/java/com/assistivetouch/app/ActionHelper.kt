package com.assistivetouch.app

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast

object ActionHelper {

    fun volumeUp(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI or AudioManager.FLAG_PLAY_SOUND)
    }

    fun volumeDown(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI or AudioManager.FLAG_PLAY_SOUND)
    }

    fun lockScreen(context: Context) {
        // First try Accessibility if Android 9+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val service = ActionAccessibilityService.instance
            if (service != null && service.lockScreen()) {
                return
            }
        }

        // Use Device Admin for Android 8.1 (and as reliable fallback)
        if (DeviceAdminManager.isActive(context)) {
            DeviceAdminManager.lockScreen(context)
        } else {
            Toast.makeText(context, "Please enable Screen Lock permission first", Toast.LENGTH_SHORT).show()
            DeviceAdminManager.requestAdmin(context)
        }
    }

    fun openPowerMenu(context: Context) {
        val service = ActionAccessibilityService.instance
        if (service != null) {
            service.openPowerMenu()
        } else {
            Toast.makeText(context, "Please turn on Accessibility Service first to access Power Menu", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
