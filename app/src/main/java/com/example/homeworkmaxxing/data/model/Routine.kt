package com.example.homeworkmaxxing.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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

@Entity(
    tableName = "routines",
    foreignKeys = [
        ForeignKey(
            entity = Cours::class,
            parentColumns = ["id"],
            childColumns = ["coursId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["coursId"])]
)
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val nom: String,
    val description: String,
    val date: LocalDateTime,
    val repetabilite: Repetabilite,
    val categorie: CategorieRoutine,
    val priorite: Priorite,
    val coursId: Long? = null
)
