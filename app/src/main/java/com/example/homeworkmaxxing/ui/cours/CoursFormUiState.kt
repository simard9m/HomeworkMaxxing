package com.example.homeworkmaxxing.ui.cours

data class CoursFormUiState(
    val coursId: Long? = null,
    val nom: String = "",
    val couleurHex: Long = 0xFFDCD5F7,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val nomError: String? = null,
    val saveSuccess: Boolean = false
)