package com.example.homeworkmaxxing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Application
import android.os.Build
import com.example.homeworkmaxxing.notification.RoutineReminderReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HomeworkMaxxingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            RoutineReminderReceiver.CHANNEL_ID,
            RoutineReminderReceiver.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = RoutineReminderReceiver.CHANNEL_DESCRIPTION
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
