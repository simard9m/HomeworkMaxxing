package com.example.homeworkmaxxing.ui.routine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routineDao: RoutineDao,
    private val coursDao: CoursDao
) : ViewModel() {

    private val routineId: Int = checkNotNull(savedStateHandle[Screen.RoutineDetail.routineIdArg])

    private val _uiState = MutableStateFlow(RoutineDetailUiState())
    val uiState: StateFlow<RoutineDetailUiState> = _uiState.asStateFlow()

    init {
        observeRoutine()
    }

    private fun observeRoutine() {
        viewModelScope.launch {
            combine(
                routineDao.observeRoutineById(routineId),
                coursDao.getAllCours()
            ) { routine, cours ->
                RoutineDetailUiState(
                    routine = routine,
                    cours = routine?.coursId?.let { coursId -> cours.find { it.id == coursId } },
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
