package com.example.homeworkmaxxing.domain.validation

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.util.ValidationRules
import java.time.LocalDate
import java.time.LocalDateTime

class RoutineValidator {
    fun validate(
        nom: String,
        description: String,
        date: LocalDateTime?,
        sessionLastDay: LocalDate?,
        isEditMode: Boolean,
        categorie: CategorieRoutine?,
        priorite: Priorite?,
        now: LocalDateTime = LocalDateTime.now()
    ): String? {
        if (nom.isBlank()) {
            return "Le nom est requis."
        }
        if (nom.length > ValidationRules.MAX_ROUTINE_NOM_LENGTH) {
            return "Le nom de la routine est trop long."
        }
        if (description.length > ValidationRules.MAX_ROUTINE_DESCRIPTION_LENGTH) {
            return "La description est trop longue."
        }
        if (date == null) {
            return "La date et l'heure sont requises."
        }
        if (!isEditMode && date.isBefore(now)) {
            return "La date et l'heure ne peuvent pas être dans le passé."
        }
        if (sessionLastDay != null && date.toLocalDate().isAfter(sessionLastDay)) {
            return "La date ne peut pas dépasser la date de fin de session."
        }
        if (categorie == null) {
            return "La catégorie est requise."
        }
        if (priorite == null) {
            return "La priorité est requise."
        }
        return null
    }
}
