package com.example.homeworkmaxxing.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.homeworkmaxxing.MainActivity
import com.example.homeworkmaxxing.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoutineReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RoutineReminderReceiver", "onReceive called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("RoutineReminderReceiver", "Notification permission missing, skip notification")
            return
        }

        val routineId = intent.getIntExtra(EXTRA_ROUTINE_ID, -1)
        val routineNom = intent.getStringExtra(EXTRA_ROUTINE_NOM).orEmpty()
        val routineDescription = intent.getStringExtra(EXTRA_ROUTINE_DESCRIPTION).orEmpty()
        val routineDateMillis = intent.getLongExtra(EXTRA_ROUTINE_DATE_MILLIS, 0L)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            routineId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeLabel = if (routineDateMillis > 0L) {
            SimpleDateFormat("dd/MM HH:mm", Locale.FRENCH).format(Date(routineDateMillis))
        } else {
            ""
        }

        val content = buildString {
            append("Dans 30 minutes ! ")
            if (routineDescription.isNotBlank()) {
                append(routineDescription)
            } else {
                append("Ta routine commence bientôt.")
            }
            if (timeLabel.isNotBlank()) {
                append(" (Début : ")
                append(timeLabel)
                append(")")
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(
                if (routineNom.isNotBlank()) "Routine: $routineNom" else "Rappel de routine"
            )
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(
            if (routineId > 0) routineId else System.currentTimeMillis().toInt(),
            notification
        )

        if (routineId > 0 && routineDateMillis > 0L) {
            val signature = "$routineId:$routineDateMillis"
            val prefs = context.getSharedPreferences(
                RoutineReminderScheduler.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val notified = prefs
                .getStringSet(RoutineReminderScheduler.KEY_NOTIFIED_SIGNATURES, emptySet())
                .orEmpty()
                .toMutableSet()
            if (notified.add(signature)) {
                prefs.edit()
                    .putStringSet(RoutineReminderScheduler.KEY_NOTIFIED_SIGNATURES, notified)
                    .apply()
            }
        }

        Log.d("RoutineReminderReceiver", "Notification shown for routineId=$routineId")
    }

    companion object {
        const val CHANNEL_ID = "routine_reminders"
        const val CHANNEL_NAME = "Rappels de routines"
        const val CHANNEL_DESCRIPTION = "Notifications des routines planifiées"

        const val EXTRA_ROUTINE_ID = "extra_routine_id"
        const val EXTRA_ROUTINE_NOM = "extra_routine_nom"
        const val EXTRA_ROUTINE_DESCRIPTION = "extra_routine_description"
        const val EXTRA_ROUTINE_DATE_MILLIS = "extra_routine_date_millis"
    }
}
