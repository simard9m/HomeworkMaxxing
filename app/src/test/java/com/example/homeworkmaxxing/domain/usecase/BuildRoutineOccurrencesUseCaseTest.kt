package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildRoutineOccurrencesUseCaseTest {

    private val useCase = BuildRoutineOccurrencesUseCase()

    @Test
    fun `le use case doit retourner une seule routine quand la répétition est absente`() {
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
        val routines = useCase(routine, LocalDate.of(2035, 5, 31))

        //Assert
        assertEquals(1, routines.size)
        assertEquals(routine, routines.first())
    }

    @Test
    fun `le use case doit retourner une seule routine quand la fin de session est nulle`() {
        //Arrange
        val routine = Routine(
            id = 1,
            nom = "Routine sans session",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            repetabilite = Repetabilite.QUOTIDIEN,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )

        //Act
        val routines = useCase(routine, null)

        //Assert
        assertEquals(1, routines.size)
        assertEquals(routine, routines.first())
    }

    @Test
    fun `le use case doit créer plusieurs routines quotidiennes jusqu à la fin de session`() {
        //Arrange
        val routine = Routine(
            id = 1,
            nom = "Routine quotidienne",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            repetabilite = Repetabilite.QUOTIDIEN,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )

        //Act
        val routines = useCase(routine, LocalDate.of(2035, 5, 13))

        //Assert
        assertEquals(4, routines.size)
        assertTrue(routines.all { it.id == null })
        assertEquals(LocalDate.of(2035, 5, 10), routines.first().date.toLocalDate())
        assertEquals(LocalDate.of(2035, 5, 13), routines.last().date.toLocalDate())
    }

    @Test
    fun `le use case doit créer plusieurs routines hebdomadaires jusqu à la fin de session`() {
        //Arrange
        val routine = Routine(
            id = 1,
            nom = "Routine hebdomadaire",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            repetabilite = Repetabilite.HEBDOMADAIRE,
            categorie = CategorieRoutine.ETUDE,
            priorite = Priorite.HAUTE,
            coursId = 1L
        )

        //Act
        val routines = useCase(routine, LocalDate.of(2035, 5, 24))

        //Assert
        assertEquals(3, routines.size)
        assertNull(routines[0].id)
        assertEquals(LocalDate.of(2035, 5, 24), routines.last().date.toLocalDate())
    }
}
