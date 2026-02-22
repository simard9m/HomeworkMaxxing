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
    val id: Long,
    val nom: String,
    val description: String,
    val date: LocalDateTime,
    val repetabilite: Repetabilite,
    val categorie: CategorieRoutine,
    val coursId: Long?,
    val priorite: Priorite
)