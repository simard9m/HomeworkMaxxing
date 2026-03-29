package com.example.homeworkmaxxing.data.model

import java.time.LocalDateTime
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Repetabilite {
    AUCUNE, QUOTIDIEN, HEBDOMADAIRE, MENSUEL
}

enum class Priorite {
    BASSE, MOYENNE, HAUTE, URGENTE
}

enum class CategorieRoutine {
    EXAMEN, DEVOIR, PROJET, ETUDE, AUTRE
}

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id : Int? = null,
    val nom: String,
    val description: String,
    val date: LocalDateTime,
    val repetabilite: Repetabilite,
    val categorie: CategorieRoutine,
    val priorite: Priorite,
    val coursId: Long? = null
)