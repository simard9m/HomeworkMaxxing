package com.example.homeworkmaxxing.ui.cours
import com.example.homeworkmaxxing.data.model.Cours

data class MesCoursUiState (
    val cours: List<Cours> = emptyList(),
    val isLoading: Boolean = true,
    val coursToDelete: Cours? = null
)