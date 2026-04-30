package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.testutil.FakeRoutineDao
import java.time.LocalDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateRoutineUseCaseTest {

    @Test
    fun `le use case doit modifier une routine existante`() = runBlocking {
        //Arrange
        val routineInitiale = Routine(
            id = 1,
            nom = "Ancienne routine",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 18, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.ETUDE,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )
        val fakeDao = FakeRoutineDao(listOf(routineInitiale))
        val useCase = UpdateRoutineUseCase(fakeDao)
        val routineModifiee = routineInitiale.copy(nom = "Routine modifiée", estCompletee = true)

        //Act
        useCase(routineModifiee)

        //Assert
        val routines = fakeDao.getAllRoutines().first()
        assertEquals(1, routines.size)
        assertEquals("Routine modifiée", routines.first().nom)
        assertTrue(routines.first().estCompletee)
    }
}
