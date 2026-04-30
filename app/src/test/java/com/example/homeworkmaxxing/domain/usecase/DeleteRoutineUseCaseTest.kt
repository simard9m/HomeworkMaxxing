package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.testutil.FakeRoutineDao
import java.time.LocalDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteRoutineUseCaseTest {

    @Test
    fun `le use case doit supprimer une routine existante`() = runBlocking {
        //Arrange
        val routine = Routine(
            id = 1,
            nom = "Routine à supprimer",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 18, 0),
            repetabilite = Repetabilite.AUCUNE,
            categorie = CategorieRoutine.ETUDE,
            priorite = Priorite.MOYENNE,
            coursId = 1L
        )
        val fakeDao = FakeRoutineDao(listOf(routine))
        val useCase = DeleteRoutineUseCase(fakeDao)

        //Act
        useCase(routine)

        //Assert
        val routines = fakeDao.getAllRoutines().first()
        assertTrue(routines.isEmpty())
    }
}
