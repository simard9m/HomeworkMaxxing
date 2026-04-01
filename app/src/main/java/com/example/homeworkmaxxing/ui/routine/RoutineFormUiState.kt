package com.example.homeworkmaxxing.ui.routine

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite

data class RoutineFormUiState(
    val nom: String = "",
    val description: String = "",
    val dateText: String = "",
    val heureText: String = "",
    val categorie: CategorieRoutine? = null,
    val priorite: Priorite? = null,
    val repetabilite: Repetabilite = Repetabilite.AUCUNE,
    val coursId: Long? = null,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showRepetitionDropdown: Boolean = false,
    val showCategorieDropdown: Boolean = false,
    val showCoursDropdown: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false
)
