package com.example.homeworkmaxxing.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.util.FakeDataUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class DashboardViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(
        DashboardUiState(
            routines = FakeDataUtil.getRoutines() ,
            cours = FakeDataUtil.getCours(),
            isLoading = false
        )
    )
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
}