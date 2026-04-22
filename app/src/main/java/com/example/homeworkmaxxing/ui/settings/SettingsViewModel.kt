package com.example.homeworkmaxxing.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.local.SessionDao
import com.example.homeworkmaxxing.data.model.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val coursDao: CoursDao,
    private val routineDao: RoutineDao,
    private val sessionDao: SessionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSession()
    }

    fun replaceSessionEndDate(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            val dateFin = LocalDate.of(year, month, day)
                .atStartOfDay(APP_ZONE_ID)
                .toInstant()
                .toEpochMilli()

            sessionDao.upsertSession(
                Session(
                    id = SESSION_UNIQUE_ID,
                    dateFin = dateFin
                )
            )
            _uiState.update {
                it.copy(message = "La date de fin de session a été remplacée.")
            }
        }
    }

    fun deleteAllCoursAndRoutines() {
        viewModelScope.launch {
            routineDao.deleteAllRoutines()
            coursDao.deleteAllCours()
            _uiState.update {
                it.copy(message = "Tous les cours et toutes les routines ont été supprimés.")
            }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun observeSession() {
        viewModelScope.launch {
            sessionDao.observeSession().collectLatest { session ->
                _uiState.update {
                    it.copy(
                        sessionEndDateMillis = session?.dateFin,
                        isLoading = false
                    )
                }
            }
        }
    }

    companion object {
        private const val SESSION_UNIQUE_ID = 1
        private val APP_ZONE_ID: ZoneId = ZoneId.of("America/Toronto")
    }
}
