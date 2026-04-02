package com.example.homeworkmaxxing.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import java.time.LocalDateTime

object FakeDataUtil {

    private data class RoutineSeed(
        val id: Int,
        val nom: String,
        val description: String,
        val daysFromNow: Long,
        val repetabilite: Repetabilite,
        val categorie: CategorieRoutine,
        val coursName: String?,
        val priorite: Priorite,
        val hour: Int? = null,
        val minute: Int? = null
    )

    fun getCours(): List<Cours> {
        return listOf(
            Cours(
                id = 1,
                nom = "Maths",
                couleurHex = 0xFFEADFFF
            ),
            Cours(
                id = 2,
                nom = "Algo",
                couleurHex = 0xFFDDEBFF
            ),
            Cours(
                id = 3,
                nom = "Dev mobile",
                couleurHex = 0xFFE7F6E7
            ),
            Cours(
                id = 4,
                nom = "Francais",
                couleurHex = 0xFFFFE4D6
            ),
            Cours(
                id = 5,
                nom = "Histoire",
                couleurHex = 0xFFDFF7F2
            ),
            Cours(
                id = 6,
                nom = "Science",
                couleurHex = 0xFFFFF3C9
            ),
            Cours(
                id = 7,
                nom = "Programmation",
                couleurHex = 0xFFE3E0FF
            ),
            Cours(
                id = 8,
                nom = "Philosophie",
                couleurHex = 0xFFFDE2F3
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRoutines(coursIdsByName: Map<String, Long>): List<Routine> {
        val seeds = listOf(
            RoutineSeed(
                id = 1,
                nom = "Examen Math",
                description = "Reviser chapitres 1 a 4",
                daysFromNow = 1,
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.EXAMEN,
                coursName = "Maths",
                priorite = Priorite.URGENTE
            ),
            RoutineSeed(
                id = 2,
                nom = "Devoir Anglais",
                description = "Remettre la redaction",
                daysFromNow = 2,
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.DEVOIR,
                coursName = "Algo",
                priorite = Priorite.HAUTE
            ),
            RoutineSeed(
                id = 3,
                nom = "Etude Algo",
                description = "Faire exercices de structures de donnees",
                daysFromNow = 3,
                repetabilite = Repetabilite.HEBDOMADAIRE,
                categorie = CategorieRoutine.ETUDE,
                coursName = "Algo",
                priorite = Priorite.MOYENNE
            ),
            RoutineSeed(
                id = 5,
                nom = "Travail Histoire",
                description = "Preparer le resume sur la Revolution industrielle",
                daysFromNow = 5,
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.PROJET,
                coursName = "Histoire",
                priorite = Priorite.HAUTE
            ),
            RoutineSeed(
                id = 6,
                nom = "Lab Science",
                description = "Completer le rapport de laboratoire",
                daysFromNow = 6,
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.DEVOIR,
                coursName = "Science",
                priorite = Priorite.HAUTE
            ),
            RoutineSeed(
                id = 7,
                nom = "Pratique Kotlin",
                description = "Faire 3 exercices sur les classes et listes",
                daysFromNow = 1,
                repetabilite = Repetabilite.HEBDOMADAIRE,
                categorie = CategorieRoutine.ETUDE,
                coursName = "Programmation",
                priorite = Priorite.MOYENNE,
                hour = 19,
                minute = 0
            )
        )

        return seeds.map { seed ->
            val date = LocalDateTime.now()
                .plusDays(seed.daysFromNow)
                .let { base ->
                    if (seed.hour != null && seed.minute != null) {
                        base.withHour(seed.hour).withMinute(seed.minute)
                    } else {
                        base
                    }
                }

            Routine(
                id = seed.id,
                nom = seed.nom,
                description = seed.description,
                date = date,
                repetabilite = seed.repetabilite,
                categorie = seed.categorie,
                priorite = seed.priorite,
                coursId = seed.coursName?.let(coursIdsByName::get)
            )
        }
    }
}
