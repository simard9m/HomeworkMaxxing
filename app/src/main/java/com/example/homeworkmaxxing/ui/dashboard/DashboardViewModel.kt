package com.example.homeworkmaxxing.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.notification.RoutineReminderScheduler
import com.example.homeworkmaxxing.util.FakeDataUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val routineDao: RoutineDao,
    private val coursDao: CoursDao,
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

    init {
        observeDatabase()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seedDatabaseIfEmpty()
        }
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

    private fun observeDatabase() {
        viewModelScope.launch {
            combine(
                routineDao.getAllRoutines(),
                coursDao.getAllCours()
            ) { routines, cours ->
                routineReminderScheduler.syncReminders(routines)
                DashboardUiState(
                    routines = routines,
                    cours = cours,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            if (coursDao.countCours() == 0) {
                coursDao.insertAllCours(
                    FakeDataUtil.getCours()
                )
            }
            if (routineDao.countRoutines() == 0) {
                val coursIdsByName = coursDao
                    .getAllCoursList()
                    .associate { cours -> cours.nom to cours.id }
                routineDao.insertRoutines(
                    FakeDataUtil.getRoutines(coursIdsByName)
                )
            }
        }
    }
}
