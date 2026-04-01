package com.example.homeworkmaxxing.ui.routine

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.util.FakeDataUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class RoutineFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineFormUiState())
    val uiState: StateFlow<RoutineFormUiState> = _uiState.asStateFlow()

    val coursList = FakeDataUtil.getCours()

    private var selectedDateTime: LocalDateTime? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.FRENCH)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH)

    fun loadRoutine(routine: Routine) {
        selectedDateTime = routine.date
        _uiState.update {
            it.copy(
                nom = routine.nom,
                description = routine.description,
                dateText = routine.date.format(dateFormatter),
                heureText = routine.date.format(timeFormatter),
                categorie = routine.categorie,
                priorite = routine.priorite,
                repetabilite = routine.repetabilite,
                coursId = routine.coursId
            )
        }
    }

    fun onNomChange(value: String) = _uiState.update { it.copy(nom = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val existing = selectedDateTime ?: LocalDateTime.now()
        selectedDateTime = existing.withYear(year).withMonth(month).withDayOfMonth(day)
        _uiState.update {
            it.copy(
                dateText = selectedDateTime!!.format(dateFormatter),
                showDatePicker = false
            )
        }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val existing = selectedDateTime ?: LocalDateTime.now()
        selectedDateTime = existing.withHour(hour).withMinute(minute)
        _uiState.update {
            it.copy(
                heureText = selectedDateTime!!.format(timeFormatter),
                showTimePicker = false
            )
        }
    }

    fun onCategorieSelected(categorie: CategorieRoutine) = _uiState.update {
        it.copy(categorie = categorie, showCategorieDropdown = false)
    }

    fun onPrioriteSelected(priorite: Priorite) = _uiState.update {
        // Tapping the same priority again deselects it
        val newPriorite = if (it.priorite == priorite) null else priorite
        it.copy(priorite = newPriorite)
    }

    fun onRepetabiliteSelected(rep: Repetabilite) = _uiState.update {
        it.copy(repetabilite = rep, showRepetitionDropdown = false)
    }

    fun onCoursSelected(coursId: Long?) = _uiState.update {
        it.copy(coursId = coursId, showCoursDropdown = false)
    }

    fun toggleDatePicker() = _uiState.update { it.copy(showDatePicker = !it.showDatePicker, showTimePicker = false) }
    fun toggleTimePicker() = _uiState.update { it.copy(showTimePicker = !it.showTimePicker, showDatePicker = false) }
    fun toggleRepetitionDropdown() = _uiState.update { it.copy(showRepetitionDropdown = !it.showRepetitionDropdown) }
    fun toggleCategorieDropdown() = _uiState.update { it.copy(showCategorieDropdown = !it.showCategorieDropdown) }
    fun toggleCoursDropdown() = _uiState.update { it.copy(showCoursDropdown = !it.showCoursDropdown) }
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    fun onSave() {
        val state = _uiState.value
        if (state.nom.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Le nom est requis.") }
            return
        }
        if (selectedDateTime == null) {
            _uiState.update { it.copy(errorMessage = "La date et l'heure sont requises.") }
            return
        }
        // TODO
        _uiState.update { it.copy(isSaved = true, errorMessage = null) }
    }

    fun onDelete() {
        // TODO
        _uiState.update { it.copy(isDeleted = true) }
    }
}