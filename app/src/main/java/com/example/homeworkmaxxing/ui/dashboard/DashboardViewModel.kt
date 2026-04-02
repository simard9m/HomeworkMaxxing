package com.example.homeworkmaxxing.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.local.SessionDao
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.data.model.Session
import com.example.homeworkmaxxing.notification.RoutineReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val routineDao: RoutineDao,
    private val coursDao: CoursDao,
    private val sessionDao: SessionDao,
    private val routineReminderScheduler: RoutineReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            routines = emptyList(),
            cours = emptyList(),
            isLoading = true
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    private val currentTimeMillis = MutableStateFlow(System.currentTimeMillis())

    init {
        startSessionClock()
        observeDatabase()
    }

    fun addRoutine(routine: Routine) {
        viewModelScope.launch {
            routineDao.insertRoutine(routine)
        }
    }

    fun updateRoutine(routine: Routine) {
        viewModelScope.launch {
            routine.id?.let {
                routineDao.updateRoutine(routine)
            }
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            routine.id?.let {
                routineDao.deleteRoutine(routine)
            }
        }
    }

    fun addCours(cours: Cours) {
        viewModelScope.launch {
            coursDao.insertCours(cours)
        }
    }

    fun updateCours(cours: Cours) {
        viewModelScope.launch {
            coursDao.updateCours(cours)
        }
    }

    fun deleteCours(cours: Cours) {
        viewModelScope.launch {
            coursDao.deleteCours(cours)
        }
    }

    fun createSessionDate(year: Int, month: Int, day: Int) {
        upsertSessionDate(year, month, day)
    }

    fun postponeSessionDate(year: Int, month: Int, day: Int) {
        upsertSessionDate(year, month, day)
    }

    fun terminateSession() {
        viewModelScope.launch {
            coursDao.deleteAllCours()
            routineDao.deleteAllRoutines()
            sessionDao.deleteSession()
        }
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            combine(
                routineDao.getAllRoutines(),
                coursDao.getAllCours(),
                sessionDao.observeSession(),
                currentTimeMillis
            ) { routines, cours, session, nowMillis ->
                val sessionState = resolveSessionState(session, nowMillis)
                if (sessionState == SessionState.SESSION_ACTIVE) {
                    routineReminderScheduler.syncReminders(routines)
                } else {
                    routineReminderScheduler.syncReminders(emptyList())
                }
                DashboardUiState(
                    routines = routines,
                    cours = cours,
                    isLoading = false,
                    sessionState = sessionState,
                    sessionDateFin = session?.dateFin
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun startSessionClock() {
        viewModelScope.launch {
            while (true) {
                delay(SESSION_STATE_TICK_MS)
                currentTimeMillis.value = System.currentTimeMillis()
            }
        }
    }

    private fun upsertSessionDate(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            val dateFin = LocalDate.of(year, month, day)
                .atStartOfDay(APP_ZONE_ID)
                .toInstant()
                .toEpochMilli()
            sessionDao.upsertSession(
                Session(
                    id = SESSION_UNIQUE_ID,
                    dateFin = dateFin
                )
            )
        }
    }

    private fun resolveSessionState(session: Session?, nowMillis: Long): SessionState {
        if (session == null) return SessionState.NO_SESSION

        val sessionEndDate = Instant.ofEpochMilli(session.dateFin)
            .atZone(APP_ZONE_ID)
            .toLocalDate()
        val expirationMillis = sessionEndDate
            .plusDays(1)
            .atStartOfDay(APP_ZONE_ID)
            .toInstant()
            .toEpochMilli()

        return if (nowMillis < expirationMillis) {
            SessionState.SESSION_ACTIVE
        } else {
            SessionState.SESSION_EXPIRED
        }
    }

    companion object {
        private const val SESSION_UNIQUE_ID = 1
        private const val SESSION_STATE_TICK_MS = 30_000L
        private val APP_ZONE_ID: ZoneId = ZoneId.of("America/Toronto")
    }
}
