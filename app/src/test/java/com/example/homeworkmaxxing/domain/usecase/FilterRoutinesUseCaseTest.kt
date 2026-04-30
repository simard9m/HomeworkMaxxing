package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterRoutinesUseCaseTest {

    private val useCase = FilterRoutinesUseCase()

    @Test
    fun `le filtre doit retourner une liste vide quand aucune routine n est fournie`() {
        //Arrange
        val routines = emptyList<Routine>()

        //Act
        val resultat = useCase(routines, emptySet(), emptySet(), emptySet(), false)

        //Assert
        assertTrue(resultat.isEmpty())
    }

    @Test
    fun `le filtre doit retourner la seule routine quand la liste contient un seul élément valide`() {
        //Arrange
        val routine = Routine(
            id = 1,
            nom = "Routine unique",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )

        //Act
        val resultat = useCase(listOf(routine), emptySet(), emptySet(), emptySet(), false)

        //Assert
        assertEquals(1, resultat.size)
        assertEquals(routine, resultat.first())
    }

    @Test
    fun `le filtre doit garder seulement les routines de la catégorie choisie`() {
        //Arrange
        val devoir = Routine(
            id = 1,
            nom = "Devoir",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )
        val examen = Routine(
            id = 2,
            nom = "Examen",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 11, 14, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.EXAMEN,
            priorite = Priorite.HAUTE,
            coursId = 2L
        )

        //Act
        val resultat = useCase(
            allRoutines = listOf(devoir, examen),
            selectedCategories = setOf(CategorieRoutine.EXAMEN),
            selectedCoursIds = emptySet(),
            selectedRepetabilites = emptySet(),
            showCompleted = false
        )

        //Assert
        assertEquals(1, resultat.size)
        assertEquals(examen, resultat.first())
    }

    @Test
    fun `le filtre doit garder seulement les routines du cours choisi`() {
        //Arrange
        val routine1 = Routine(
            id = 1,
            nom = "Routine 1",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )
        val routine2 = Routine(
            id = 2,
            nom = "Routine 2",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 11, 14, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.EXAMEN,
            priorite = Priorite.HAUTE,
            coursId = 2L
        )

        //Act
        val resultat = useCase(
            allRoutines = listOf(routine1, routine2),
            selectedCategories = emptySet(),
            selectedCoursIds = setOf(2L),
            selectedRepetabilites = emptySet(),
            showCompleted = false
        )

        //Assert
        assertEquals(1, resultat.size)
        assertEquals(routine2, resultat.first())
    }

    @Test
    fun `le filtre doit cacher les routines complétées quand showCompleted est faux`() {
        //Arrange
        val routineActive = Routine(
            id = 1,
            nom = "Routine active",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.MOYENNE,
            coursId = 1L,
            estCompletee = false
        )
        val routineCompletee = Routine(
            id = 2,
            nom = "Routine complétée",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 11, 14, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.EXAMEN,
            priorite = Priorite.HAUTE,
            coursId = 2L,
            estCompletee = true
        )

        //Act
        val resultat = useCase(
            allRoutines = listOf(routineActive, routineCompletee),
            selectedCategories = emptySet(),
            selectedCoursIds = emptySet(),
            selectedRepetabilites = emptySet(),
            showCompleted = false
        )

        //Assert
        assertEquals(1, resultat.size)
        assertEquals(routineActive, resultat.first())
    }
}
