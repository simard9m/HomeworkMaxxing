package com.example.homeworkmaxxing.domain.validation

import com.example.homeworkmaxxing.util.ValidationRules

class CoursValidator {
    fun validateNom(nom: String): String? {
        if (nom.isBlank()) {
            return "Le nom du cours est requis."
        }
        if (nom.length > ValidationRules.MAX_COURS_NOM_LENGTH) {
            return "Maximum ${ValidationRules.MAX_COURS_NOM_LENGTH} caractères."
        }
        return null
    }
}
