package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine

class FilterRoutinesUseCase {
    operator fun invoke(
        allRoutines: List<Routine>,
        selectedCategories: Set<CategorieRoutine>,
        selectedCoursIds: Set<Long>,
        selectedRepetabilites: Set<Repetabilite>,
        showCompleted: Boolean
    ): List<Routine> {
        return allRoutines
            .filter { routine -> showCompleted || !routine.estCompletee }
            .filter { routine ->
                selectedCategories.isEmpty() || selectedCategories.contains(routine.categorie)
            }
            .filter { routine ->
                selectedCoursIds.isEmpty() ||
                    (routine.coursId != null && selectedCoursIds.contains(routine.coursId))
            }
            .filter { routine ->
                selectedRepetabilites.isEmpty() ||
                    selectedRepetabilites.contains(routine.repetabilite)
            }
            .sortedBy { it.date }
    }
}
