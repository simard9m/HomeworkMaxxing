package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.testutil.FakeCoursDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteCoursUseCaseTest {

    @Test
    fun `le use case doit supprimer un cours existant`() = runBlocking {
        //Arrange
        val cours = Cours(id = 1, nom = "Histoire", couleurHex = 0xFFDCD5F7)
        val fakeDao = FakeCoursDao(listOf(cours))
        val useCase = DeleteCoursUseCase(fakeDao)

        //Act
        useCase(cours)

        //Assert
        val coursEnregistres = fakeDao.getAllCours().first()
        assertTrue(coursEnregistres.isEmpty())
    }
}
