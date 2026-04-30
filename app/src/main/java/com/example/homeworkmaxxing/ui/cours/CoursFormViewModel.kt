package com.example.homeworkmaxxing.ui.cours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.domain.validation.CoursValidator
import com.example.homeworkmaxxing.util.ValidationRules
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CoursFormViewModel @Inject constructor(
    private val coursDao: CoursDao
) : ViewModel() {

    private val coursValidator = CoursValidator()
    private val _uiState = MutableStateFlow(CoursFormUiState())
    val uiState: StateFlow<CoursFormUiState> = _uiState.asStateFlow()

    fun onNomChange(newValue: String) {
        val limitedValue = newValue.take(ValidationRules.MAX_COURS_NOM_LENGTH)
        _uiState.update {
            it.copy(
                nom = limitedValue,
                nomError = null
            )
        }
    }

    fun onColorSelected(colorHex: Long) {
        _uiState.update { it.copy(couleurHex = colorHex) }
    }

    fun loadCoursForEdit(coursId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    saveSuccess = false
                )
            }

            val cours = coursDao.getCoursById(coursId)

            if (cours != null) {
                _uiState.update {
                    it.copy(
                        coursId = cours.id,
                        nom = cours.nom,
                        couleurHex = cours.couleurHex,
                        isEditMode = true,
                        isLoading = false,
                        nomError = null
                    )
                }
            } else {
                resetForCreate()
            }
        }
    }

    fun saveCours() {
        val currentState = _uiState.value
        val trimmedName = currentState.nom.trim()
        val validationError = coursValidator.validateNom(trimmedName)

        if (validationError != null) {
            _uiState.update { it.copy(nomError = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, nomError = null) }

            runCatching {
                if (currentState.isEditMode && currentState.coursId != null) {
                    coursDao.updateCours(
                        Cours(
                            id = currentState.coursId,
                            nom = trimmedName,
                            couleurHex = currentState.couleurHex
                        )
                    )
                } else {
                    coursDao.insertCours(
                        Cours(
                            id = 0,
                            nom = trimmedName,
                            couleurHex = currentState.couleurHex
                        )
                    )
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        nomError = "Impossible d'enregistrer ce cours."
                    )
                }
            }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun resetForCreate() {
        _uiState.value = CoursFormUiState()
    }
}
