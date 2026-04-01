package com.example.homeworkmaxxing.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.util.FakeDataUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(
        DashboardUiState(
            routines = FakeDataUtil.getRoutines(),
            cours = FakeDataUtil.getCours(),
            isLoading = false
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    //Compteur pour générer des IDs uniques aux nouvelles routines
    @RequiresApi(Build.VERSION_CODES.O)
    private var nextId: Int = FakeDataUtil.getRoutines().mapNotNull { it.id }.maxOrNull()?.plus(1) ?: 1

    @RequiresApi(Build.VERSION_CODES.O)
    fun addRoutine(routine: Routine) {
        val withId = routine.copy(id = nextId++)
        _uiState.update { state ->
            state.copy(routines = state.routines + withId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRoutine(updated: Routine) {
        _uiState.update { state ->
            state.copy(
                routines = state.routines.map { existing ->
                    if (existing.id == updated.id) updated else existing
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteRoutine(routineId: Int) {
        _uiState.update { state ->
            state.copy(routines = state.routines.filter { it.id != routineId })
        }
    }
}