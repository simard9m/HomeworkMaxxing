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
        val currentSignatures = routines.mapNotNull { routine ->
            val routineId = routine.id ?: return@mapNotNull null
            reminderSignature(routineId, toRoutineStartMillis(routine))
        }.toSet()
        val previousIds = prefs
            .getStringSet(KEY_SCHEDULED_IDS, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()
        val notifiedSignatures = prefs
            .getStringSet(KEY_NOTIFIED_SIGNATURES, emptySet())
            .orEmpty()
            .toMutableSet()

        notifiedSignatures.retainAll(currentSignatures)

        val removedIds = previousIds - currentIds
        removedIds.forEach { routineId ->
            cancelReminder(routineId)
            notifiedSignatures.removeAll { signature ->
                signature.startsWith("$routineId:")
            }
        }

        routines.forEach { routine ->
            scheduleReminder(routine, notifiedSignatures)
        }

        prefs.edit()
            .putStringSet(KEY_SCHEDULED_IDS, currentIds.map { it.toString() }.toSet())
            .putStringSet(KEY_NOTIFIED_SIGNATURES, notifiedSignatures)
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

    private fun scheduleReminder(routine: Routine, notifiedSignatures: MutableSet<String>) {
        val routineId = routine.id ?: return
        val routineStartMillis = toRoutineStartMillis(routine)
        val triggerAtMillis = routineStartMillis - REMINDER_LEAD_TIME_MS
        val signature = reminderSignature(routineId, routineStartMillis)
        val now = System.currentTimeMillis()
        val reminderIntent = buildReminderIntent(routine, routineId, routineStartMillis)

        if (triggerAtMillis <= now) {
            val delay = now - triggerAtMillis
            if (routineStartMillis > now || delay <= PAST_TRIGGER_GRACE_MS) {
                // If we're inside the 30-minute window, notify only once.
                if (notifiedSignatures.add(signature)) {
                    context.sendBroadcast(reminderIntent)
                } else {
                    Log.d(
                        "RoutineReminderScheduler",
                        "Skip duplicate reminder for routineId=$routineId signature=$signature"
                    )
                }
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

        Log.d(
            "RoutineReminderScheduler",
            "Reminder scheduled routineId=$routineId reminderAt=$triggerAtMillis routineStart=$routineStartMillis"
        )
    }

    private fun toRoutineStartMillis(routine: Routine): Long {
        return routine.date
            .atZone(appZoneId)
            .toInstant()
            .toEpochMilli()
    }

    private fun reminderSignature(routineId: Int, routineStartMillis: Long): String {
        return "$routineId:$routineStartMillis"
    }

    private fun buildReminderIntent(
        routine: Routine,
        routineId: Int,
        routineStartMillis: Long
    ): Intent {
        return Intent(context, RoutineReminderReceiver::class.java).apply {
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_ID, routineId)
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_NOM, routine.nom)
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_DESCRIPTION, routine.description)
            putExtra(RoutineReminderReceiver.EXTRA_ROUTINE_DATE_MILLIS, routineStartMillis)
        }
    }

    companion object {
        const val PREFS_NAME = "routine_reminder_prefs"
        private const val KEY_SCHEDULED_IDS = "scheduled_routine_ids"
        const val KEY_NOTIFIED_SIGNATURES = "notified_routine_signatures"
        private const val APP_TIME_ZONE = "America/Toronto"
        private const val PAST_TRIGGER_GRACE_MS = 60_000L
        private const val REMINDER_LEAD_TIME_MS = 30 * 60 * 1000L + 50 * 1000L
    }
}
