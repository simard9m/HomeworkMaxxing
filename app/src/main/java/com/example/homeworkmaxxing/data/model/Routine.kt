package com.example.homeworkmaxxing.data.model

import java.time.LocalDateTime

enum class Repetabilite {
    AUCUNE, QUOTIDIEN, HEBDOMADAIRE, MENSUEL
}

enum class Priorite {
    BASSE, MOYENNE, HAUTE, URGENTE
}

enum class CategorieRoutine {
    EXAMEN, DEVOIR, PROJET, ETUDE, AUTRE
}

data class Routine(
    val id: Int? = null,
    val nom: String,
    val description: String,
    val date: LocalDateTime,
    val repetabilite: Repetabilite,
    val categorie: CategorieRoutine,
    val priorite: Priorite,
    val coursId: Long? = null
)
