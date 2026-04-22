package com.example.homeworkmaxxing.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.notification.RoutineReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val routineDao: RoutineDao,
    private val coursDao: CoursDao,
    private val routineReminderScheduler: RoutineReminderScheduler
) : ViewModel() {

    private val selectedCategorie = MutableStateFlow<CategorieRoutine?>(null)
    private val selectedCoursId = MutableStateFlow<Long?>(null)
    private val selectedRepetabilite = MutableStateFlow<Repetabilite?>(null)
    private val showCompleted = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(RoutinesUiState(isLoading = true))
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

    init {
        observeRoutines()
    }

    fun setCategorieFilter(categorie: CategorieRoutine?) {
        selectedCategorie.value = categorie
    }

    fun setCoursFilter(coursId: Long?) {
        selectedCoursId.value = coursId
    }

    fun setRepetabiliteFilter(repetabilite: Repetabilite?) {
        selectedRepetabilite.value = repetabilite
    }

    fun setShowCompleted(show: Boolean) {
        showCompleted.value = show
    }

    fun toggleRoutineCompletion(routine: Routine) {
        viewModelScope.launch {
            routineDao.updateRoutine(routine.copy(estCompletee = !routine.estCompletee))
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            routineDao.deleteRoutine(routine)
        }
    }

    private fun observeRoutines() {
        viewModelScope.launch {
            val dataFlow = combine(
                routineDao.getAllRoutines(),
                coursDao.getAllCours()
            ) { routines, cours ->
                routines to cours
            }

            combine(
                dataFlow,
                selectedCategorie,
                selectedCoursId,
                selectedRepetabilite,
                showCompleted
            ) { data, categorie, coursId, repetabilite, showCompleted ->
                val (allRoutines, cours) = data
                routineReminderScheduler.syncReminders(allRoutines)

                val filteredRoutines = allRoutines
                    .filter { routine -> showCompleted || !routine.estCompletee }
                    .filter { routine -> categorie == null || routine.categorie == categorie }
                    .filter { routine -> coursId == null || routine.coursId == coursId }
                    .filter { routine -> repetabilite == null || routine.repetabilite == repetabilite }
                    .sortedBy { it.date }

                RoutinesUiState(
                    allRoutines = allRoutines,
                    routines = filteredRoutines,
                    cours = cours,
                    isLoading = false,
                    selectedCategorie = categorie,
                    selectedCoursId = coursId,
                    selectedRepetabilite = repetabilite,
                    showCompleted = showCompleted
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
