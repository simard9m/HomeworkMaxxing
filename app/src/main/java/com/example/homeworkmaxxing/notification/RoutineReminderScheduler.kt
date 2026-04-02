package com.example.homeworkmaxxing.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.homeworkmaxxing.data.model.Routine
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject

class RoutineReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val appZoneId = ZoneId.of(APP_TIME_ZONE)

    fun syncReminders(routines: List<Routine>) {
        val currentIds = routines.mapNotNull { it.id }.toSet()
        val previousIds = prefs
            .getStringSet(KEY_SCHEDULED_IDS, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

        val removedIds = previousIds - currentIds
        removedIds.forEach(::cancelReminder)

        routines.forEach(::scheduleReminder)

        prefs.edit()
            .putStringSet(KEY_SCHEDULED_IDS, currentIds.map { it.toString() }.toSet())
            .apply()
    }

    fun cancelReminder(routineId: Int) {
        val intent = Intent(context, RoutineReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            routineId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleReminder(routine: Routine) {
        val routineId = routine.id ?: return
        val triggerAtMillis = routine.date
            .atZone(appZoneId)
            .toInstant()
            .toEpochMilli()
        val now = System.currentTimeMillis()
        val reminderIntent = buildReminderIntent(routine, routineId, triggerAtMillis)

        if (triggerAtMillis <= now) {
            val delay = now - triggerAtMillis
            if (delay <= PAST_TRIGGER_GRACE_MS) {
                // If we're only a few seconds/minutes late, fire immediately instead of dropping it.
                context.sendBroadcast(reminderIntent)
            } else {
                cancelReminder(routineId)
            }
            return
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            routineId,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                alarmManager.canScheduleExactAlarms() -> {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            else -> {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        }

        Log.d("RoutineReminderScheduler", "Reminder scheduled for routineId=$routineId at=$triggerAtMillis")
    }

    private fun buildReminderIntent(
        routine: Routine,
        routineId: Int,
        triggerAtMillis: Long
    ): Intent {
        return Intent(context, RoutineReminderReceiver::class.java).apply {
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_ID, routineId)
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_NOM, routine.nom)
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_DESCRIPTION, routine.description)
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_DATE_MILLIS, triggerAtMillis)
        }
    }

    companion object {
        private const val PREFS_NAME = "routine_reminder_prefs"
        private const val KEY_SCHEDULED_IDS = "scheduled_routine_ids"
        private const val APP_TIME_ZONE = "America/Toronto"
        private const val PAST_TRIGGER_GRACE_MS = 60_000L
    }
}
