package com.example.homeworkmaxxing.ui.routine

import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine

data class RoutineDetailUiState(
    val routine: Routine? = null,
    val cours: Cours? = null,
    val isLoading: Boolean = true
)
