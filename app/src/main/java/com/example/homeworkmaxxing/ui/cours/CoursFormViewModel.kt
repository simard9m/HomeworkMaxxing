package com.example.homeworkmaxxing.ui.cours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.model.Cours
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

    private val _uiState = MutableStateFlow(CoursFormUiState())
    val uiState: StateFlow<CoursFormUiState> = _uiState.asStateFlow()

    fun onNomChange(newValue: String) {
        _uiState.update {
            it.copy(
                nom = newValue,
                nomError = null
            )
        }
    }

    fun onColorSelected(colorHex: Long) {
        _uiState.update { it.copy(couleurHex = colorHex) }
    }

    fun loadCoursForEdit(coursId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

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
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun saveCours() {
        val currentState = _uiState.value
        val trimmedName = currentState.nom.trim()

        if (trimmedName.isBlank()) {
            _uiState.update {
                it.copy(nomError = "Le nom du cours est requis.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, nomError = null) }

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

            _uiState.update {
                it.copy(
                    isSaving = false,
                    saveSuccess = true
                )
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