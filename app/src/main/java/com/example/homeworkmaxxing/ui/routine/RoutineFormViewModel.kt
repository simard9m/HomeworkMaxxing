package com.example.homeworkmaxxing.ui.routine

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.util.ValidationRules
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RoutineFormViewModel @Inject constructor(
    private val coursDao: CoursDao,
    private val routineDao: RoutineDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineFormUiState())
    val uiState: StateFlow<RoutineFormUiState> = _uiState.asStateFlow()

    private val _coursList = MutableStateFlow<List<Cours>>(emptyList())
    val coursList: StateFlow<List<Cours>> = _coursList.asStateFlow()

    private var currentRoutineId: Int? = null
    private var selectedDateTime: LocalDateTime? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.FRENCH)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH)

    init {
        observeCours()
    }

    private fun observeCours() {
        viewModelScope.launch {
            coursDao.getAllCours().collectLatest { cours ->
                _coursList.value = cours
            }
        }
    }

    fun loadRoutine(routine: Routine) {
        currentRoutineId = routine.id
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
                coursId = routine.coursId,
                errorMessage = null
            )
        }
    }

    fun onNomChange(value: String) = _uiState.update {
        it.copy(
            nom = value.take(ValidationRules.MAX_ROUTINE_NOM_LENGTH),
            errorMessage = null
        )
    }

    fun onDescriptionChange(value: String) = _uiState.update {
        it.copy(
            description = value.take(ValidationRules.MAX_ROUTINE_DESCRIPTION_LENGTH),
            errorMessage = null
        )
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val existing = selectedDateTime ?: LocalDateTime.now()
        selectedDateTime = existing.withYear(year).withMonth(month).withDayOfMonth(day)
        _uiState.update {
            it.copy(
                dateText = selectedDateTime!!.format(dateFormatter),
                showDatePicker = false,
                errorMessage = null
            )
        }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val existing = selectedDateTime ?: LocalDateTime.now()
        selectedDateTime = existing.withHour(hour).withMinute(minute)
        _uiState.update {
            it.copy(
                heureText = selectedDateTime!!.format(timeFormatter),
                showTimePicker = false,
                errorMessage = null
            )
        }
    }

    fun onCategorieSelected(categorie: CategorieRoutine) = _uiState.update {
        it.copy(
            categorie = categorie,
            showCategorieDropdown = false,
            errorMessage = null
        )
    }

    fun onPrioriteSelected(priorite: Priorite) = _uiState.update {
        val newPriorite = if (it.priorite == priorite) null else priorite
        it.copy(priorite = newPriorite, errorMessage = null)
    }

    fun onRepetabiliteSelected(rep: Repetabilite) = _uiState.update {
        it.copy(
            repetabilite = rep,
            showRepetitionDropdown = false,
            errorMessage = null
        )
    }

    fun onCoursSelected(coursId: Long?) = _uiState.update {
        it.copy(
            coursId = coursId,
            showCoursDropdown = false,
            errorMessage = null
        )
    }

    fun toggleDatePicker() = _uiState.update {
        it.copy(showDatePicker = !it.showDatePicker, showTimePicker = false)
    }

    fun toggleTimePicker() = _uiState.update {
        it.copy(showTimePicker = !it.showTimePicker, showDatePicker = false)
    }

    fun dismissDatePicker() = _uiState.update { it.copy(showDatePicker = false) }

    fun dismissTimePicker() = _uiState.update { it.copy(showTimePicker = false) }

    fun toggleRepetitionDropdown() = _uiState.update {
        it.copy(showRepetitionDropdown = !it.showRepetitionDropdown)
    }

    fun toggleCategorieDropdown() = _uiState.update {
        it.copy(showCategorieDropdown = !it.showCategorieDropdown)
    }

    fun toggleCoursDropdown() = _uiState.update {
        it.copy(showCoursDropdown = !it.showCoursDropdown)
    }

    private fun buildRoutineOrError(): Routine? {
        val state = _uiState.value
        val nom = state.nom.trim()
        val description = state.description.trim()

        if (nom.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Le nom est requis.") }
            return null
        }
        if (nom.length > ValidationRules.MAX_ROUTINE_NOM_LENGTH) {
            _uiState.update { it.copy(errorMessage = "Le nom de la routine est trop long.") }
            return null
        }
        if (description.length > ValidationRules.MAX_ROUTINE_DESCRIPTION_LENGTH) {
            _uiState.update { it.copy(errorMessage = "La description est trop longue.") }
            return null
        }
        if (selectedDateTime == null) {
            _uiState.update { it.copy(errorMessage = "La date et l'heure sont requises.") }
            return null
        }
        val categorie = state.categorie
        if (categorie == null) {
            _uiState.update { it.copy(errorMessage = "La categorie est requise.") }
            return null
        }
        val priorite = state.priorite
        if (priorite == null) {
            _uiState.update { it.copy(errorMessage = "La priorite est requise.") }
            return null
        }

        return Routine(
            id = currentRoutineId,
            nom = nom,
            description = description,
            date = selectedDateTime!!,
            repetabilite = state.repetabilite,
            categorie = categorie,
            priorite = priorite,
            coursId = state.coursId
        )
    }

    fun onSave() {
        val routine = buildRoutineOrError() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                if (currentRoutineId == null) {
                    routineDao.insertRoutine(routine)
                } else {
                    routineDao.updateRoutine(routine)
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true,
                        errorMessage = null
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Impossible d'enregistrer la routine."
                    )
                }
            }
        }
    }

    fun onDelete() {
        val routine = buildRoutineOrError()?.takeIf { it.id != null } ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                routineDao.deleteRoutine(routine)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDeleted = true,
                        errorMessage = null
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Impossible de supprimer la routine."
                    )
                }
            }
        }
    }
}
