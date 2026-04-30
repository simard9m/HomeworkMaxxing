package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.testutil.FakeCoursDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddCoursUseCaseTest {

    @Test
    fun `le use case doit ajouter un cours valide`() = runBlocking {
        //Arrange
        val fakeDao = FakeCoursDao()
        val useCase = AddCoursUseCase(fakeDao)
        val cours = Cours(id = 0, nom = "Mathématiques", couleurHex = 0xFFDCD5F7)

        //Act
        useCase(cours)

        //Assert
        val coursEnregistres = fakeDao.getAllCours().first()
        assertEquals(1, coursEnregistres.size)
        assertTrue(coursEnregistres.any { it.nom == "Mathématiques" })
    }
}
