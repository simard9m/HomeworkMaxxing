package com.example.homeworkmaxxing.ui.dashboard

import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine

data class DashboardUiState(
    val routines: List<Routine> = emptyList(),
    val cours: List<Cours> = emptyList(),
    val isLoading: Boolean = false
)