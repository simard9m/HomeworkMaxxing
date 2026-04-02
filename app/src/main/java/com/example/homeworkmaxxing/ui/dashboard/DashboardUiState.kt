package com.example.homeworkmaxxing.ui.dashboard

import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine

enum class SessionState {
    NO_SESSION,
    SESSION_ACTIVE,
    SESSION_EXPIRED
}

data class DashboardUiState(
    val routines: List<Routine> = emptyList(),
    val cours: List<Cours> = emptyList(),
    val isLoading: Boolean = false,
    val sessionState: SessionState = SessionState.NO_SESSION,
    val sessionDateFin: Long? = null
)
