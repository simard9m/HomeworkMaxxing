package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.testutil.FakeCoursDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateCoursUseCaseTest {

    @Test
    fun `le use case doit modifier un cours existant`() = runBlocking {
        //Arrange
        val coursInitial = Cours(id = 1, nom = "Algo", couleurHex = 0xFFDCD5F7)
        val fakeDao = FakeCoursDao(listOf(coursInitial))
        val useCase = UpdateCoursUseCase(fakeDao)
        val coursModifie = Cours(id = 1, nom = "Algorithmes", couleurHex = 0xFFABCDEF)

        //Act
        useCase(coursModifie)

        //Assert
        val coursEnregistres = fakeDao.getAllCours().first()
        assertEquals(1, coursEnregistres.size)
        assertEquals("Algorithmes", coursEnregistres.first().nom)
        assertEquals(0xFFABCDEF, coursEnregistres.first().couleurHex)
    }
}
