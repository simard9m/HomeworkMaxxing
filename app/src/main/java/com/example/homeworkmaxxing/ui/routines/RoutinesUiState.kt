package com.example.homeworkmaxxing.ui.routines

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine

data class RoutinesUiState(
    val allRoutines: List<Routine> = emptyList(),
    val routines: List<Routine> = emptyList(),
    val cours: List<Cours> = emptyList(),
    val isLoading: Boolean = false,
    val selectedCategorie: CategorieRoutine? = null,
    val selectedCoursId: Long? = null,
    val selectedRepetabilite: Repetabilite? = null,
    val showCompleted: Boolean = false
)
