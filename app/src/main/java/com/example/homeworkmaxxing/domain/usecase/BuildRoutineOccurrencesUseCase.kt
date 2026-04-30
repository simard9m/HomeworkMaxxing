package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import java.time.LocalDate

class BuildRoutineOccurrencesUseCase {
    operator fun invoke(routine: Routine, sessionEnd: LocalDate?): List<Routine> {
        val lastDay = sessionEnd ?: return listOf(routine)
        if (routine.repetabilite == Repetabilite.AUCUNE) return listOf(routine)

        val occurrences = mutableListOf<Routine>()
        var nextDate = routine.date

        while (!nextDate.toLocalDate().isAfter(lastDay)) {
            occurrences += routine.copy(
                id = null,
                date = nextDate,
                estCompletee = false
            )
            nextDate = when (routine.repetabilite) {
                Repetabilite.AUCUNE -> break
                Repetabilite.QUOTIDIEN -> nextDate.plusDays(1)
                Repetabilite.HEBDOMADAIRE -> nextDate.plusWeeks(1)
                Repetabilite.MENSUEL -> nextDate.plusMonths(1)
            }
        }

        return occurrences.ifEmpty { listOf(routine) }
    }
}
