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

class AddRoutineUseCaseTest {

    @Test
    fun `le use case doit ajouter une routine valide`() = runBlocking {
        //Arrange
        val fakeDao = FakeRoutineDao()
        val useCase = AddRoutineUseCase(fakeDao)
        val routine = Routine(
            id = null,
            nom = "Étudier le chapitre 1",
            description = "Réviser les notions importantes",
            date = LocalDateTime.of(2035, 5, 10, 18, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.ETUDE,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )

        //Act
        useCase(routine)

        //Assert
        val routines = fakeDao.getAllRoutines().first()
        assertEquals(1, routines.size)
        assertTrue(routines.any { it.nom == "Étudier le chapitre 1" })
    }
}
