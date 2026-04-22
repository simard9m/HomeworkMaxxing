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

    private val selectedCategories = MutableStateFlow<Set<CategorieRoutine>>(emptySet())
    private val selectedCoursIds = MutableStateFlow<Set<Long>>(emptySet())
    private val selectedRepetabilites = MutableStateFlow<Set<Repetabilite>>(emptySet())
    private val showCompleted = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(RoutinesUiState(isLoading = true))
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

    init {
        observeRoutines()
    }

    fun setCategorieFilter(categorie: CategorieRoutine?) {
        selectedCategories.value = if (categorie == null) {
            emptySet()
        } else {
            selectedCategories.value.toggle(categorie)
        }
    }

    fun setCoursFilter(coursId: Long?) {
        selectedCoursIds.value = if (coursId == null) {
            emptySet()
        } else {
            selectedCoursIds.value.toggle(coursId)
        }
    }

    fun setRepetabiliteFilter(repetabilite: Repetabilite?) {
        selectedRepetabilites.value = if (repetabilite == null) {
            emptySet()
        } else {
            selectedRepetabilites.value.toggle(repetabilite)
        }
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
                selectedCategories,
                selectedCoursIds,
                selectedRepetabilites,
                showCompleted
            ) { data, categories, coursIds, repetabilites, showCompleted ->
                val (allRoutines, cours) = data
                routineReminderScheduler.syncReminders(allRoutines)

                val filteredRoutines = allRoutines
                    .filter { routine -> showCompleted || !routine.estCompletee }
                    .filter { routine ->
                        categories.isEmpty() || categories.contains(routine.categorie)
                    }
                    .filter { routine ->
                        coursIds.isEmpty() || (routine.coursId != null && coursIds.contains(routine.coursId))
                    }
                    .filter { routine ->
                        repetabilites.isEmpty() || repetabilites.contains(routine.repetabilite)
                    }
                    .sortedBy { it.date }

                RoutinesUiState(
                    allRoutines = allRoutines,
                    routines = filteredRoutines,
                    cours = cours,
                    isLoading = false,
                    selectedCategories = categories,
                    selectedCoursIds = coursIds,
                    selectedRepetabilites = repetabilites,
                    showCompleted = showCompleted
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

private fun <T> Set<T>.toggle(item: T): Set<T> {
    return if (contains(item)) this - item else this + item
}
