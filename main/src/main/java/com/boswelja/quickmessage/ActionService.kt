package com.boswelja.quickmessage

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ActionService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("ActionService", "Starting")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1401, createNotification())
        Log.d("ActionService", "Handling work")
        when (intent?.action) {
            ACTION_SEND_MESSAGE -> {
                Intent(this, ConfirmationActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.also {
                    startActivity(it)
                }

            }
        }
        stopForeground(true)
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(NOTI_CHANNEL_ID, "Foreground Service Running Notifications", NotificationManager.IMPORTANCE_LOW).also {
                getSystemService(NotificationManager::class.java)?.createNotificationChannel(it)
            }
        }
        NotificationCompat.Builder(this, NOTI_CHANNEL_ID).apply {
            setContentTitle("ActionService Running")
            setContentText("This needs to be here, thanks Android :(")
            setSmallIcon(R.drawable.send_clock_outline)
        }.also {
            return it.build()
        }
    }

    companion object {
        const val ACTION_SEND_MESSAGE = "send_message"

        private const val NOTI_CHANNEL_ID = "ActionServiceForegroundChannel"
    }

}