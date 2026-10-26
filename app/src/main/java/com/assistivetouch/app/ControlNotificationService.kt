package com.assistivetouch.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ControlNotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "assistive_quick_controls"
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, ControlNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, ControlNotificationService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Assistive Quick Controls",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Quick buttons to control volume, lock screen, and power"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun getPendingAction(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, ActionReceiver::class.java).apply {
            this.action = action
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(this, requestCode, intent, flags)
    }

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val openAppFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, openAppFlags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Assistive Controls Active")
            .setContentText("Tap buttons below for quick control")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_launcher, "Vol -", getPendingAction(ActionReceiver.ACTION_VOL_DOWN, 1))
            .addAction(R.drawable.ic_launcher, "Vol +", getPendingAction(ActionReceiver.ACTION_VOL_UP, 2))
            .addAction(R.drawable.ic_launcher, "Lock", getPendingAction(ActionReceiver.ACTION_LOCK_SCREEN, 3))
            .addAction(R.drawable.ic_launcher, "Power", getPendingAction(ActionReceiver.ACTION_POWER_MENU, 4))
            .build()
    }
}
