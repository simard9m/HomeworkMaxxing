package com.example.homeworkmaxxing.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import java.time.LocalDateTime

object FakeDataUtil {

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
                nom = "Français",
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
    fun getRoutines(): List<Routine> {
        return listOf(
            Routine(
                id = 1,
                nom = "Examen Math",
                description = "Réviser chapitres 1 à 4",
                date = LocalDateTime.now().plusDays(1),
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.EXAMEN,
                coursId = 1,
                priorite = Priorite.URGENTE
            ),
            Routine(
                id = 2,
                nom = "Devoir Anglais",
                description = "Remettre la rédaction",
                date = LocalDateTime.now().plusDays(2),
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.DEVOIR,
                coursId = 2,
                priorite = Priorite.HAUTE
            ),
            Routine(
                id = 3,
                nom = "Étude Algo",
                description = "Faire exercices de structures de données",
                date = LocalDateTime.now().plusDays(3),
                repetabilite = Repetabilite.HEBDOMADAIRE,
                categorie = CategorieRoutine.ETUDE,
                coursId = 2,
                priorite = Priorite.MOYENNE
            ),
            Routine(
                id = 5,
                nom = "Travail Histoire",
                description = "Préparer le résumé sur la Révolution industrielle",
                date = LocalDateTime.now().plusDays(5),
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.PROJET,
                coursId = 5,
                priorite = Priorite.HAUTE
            ),
            Routine(
                id = 6,
                nom = "Lab Science",
                description = "Compléter le rapport de laboratoire",
                date = LocalDateTime.now().plusDays(6),
                repetabilite = Repetabilite.AUCUNE,
                categorie = CategorieRoutine.DEVOIR,
                coursId = 6,
                priorite = Priorite.HAUTE
            ),
            Routine(
                id = 7,
                nom = "Pratique Kotlin",
                description = "Faire 3 exercices sur les classes et listes",
                date = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0),
                repetabilite = Repetabilite.HEBDOMADAIRE,
                categorie = CategorieRoutine.ETUDE,
                coursId = 7,
                priorite = Priorite.MOYENNE
            )
        )
    }
}