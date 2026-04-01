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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MesCoursViewModel @Inject constructor(
    private val coursDao: CoursDao
): ViewModel(){
    private val _uiState = MutableStateFlow(MesCoursUiState())
    val uiState: StateFlow<MesCoursUiState> = _uiState.asStateFlow()

    init {
        observeCours()
    }

    private fun observeCours()
    {
        viewModelScope.launch {
            coursDao.getAllCours().collect {
                coursList -> _uiState.update {
                    it.copy(
                        cours = coursList,
                        isLoading = false
                    )
            }
            }
        }
    }

    fun onDeleteClicked(cours:Cours){
        _uiState.update { it.copy(coursToDelete = cours)}
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(coursToDelete = null) }
    }

    fun confirmDeleteCours() {
        val cours = _uiState.value.coursToDelete ?: return

        viewModelScope.launch {
            coursDao.deleteCours(cours)
            _uiState.update { it.copy(coursToDelete = null) }
        }
    }
}