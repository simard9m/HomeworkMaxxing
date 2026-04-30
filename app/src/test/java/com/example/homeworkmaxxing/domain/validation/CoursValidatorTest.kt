package com.example.homeworkmaxxing.domain.validation

import com.example.homeworkmaxxing.util.ValidationRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CoursValidatorTest {

    private val validator = CoursValidator()

    @Test
    fun `la validation doit réussir quand le nom du cours est valide`() {
        //Arrange
        val nom = "Programmation mobile"

        //Act
        val erreur = validator.validateNom(nom)

        //Assert
        assertNull(erreur)
    }

    @Test
    fun `la validation doit échouer quand le nom du cours est vide`() {
        //Arrange
        val nom = "   "

        //Act
        val erreur = validator.validateNom(nom)

        //Assert
        assertEquals("Le nom du cours est requis.", erreur)
    }

    @Test
    fun `la validation doit échouer quand le nom du cours dépasse la longueur maximale`() {
        //Arrange
        val nom = "a".repeat(ValidationRules.MAX_COURS_NOM_LENGTH + 1)

        //Act
        val erreur = validator.validateNom(nom)

        //Assert
        assertEquals("Maximum ${ValidationRules.MAX_COURS_NOM_LENGTH} caractères.", erreur)
    }

    @Test
    fun `la validation doit réussir quand le nom du cours est exactement à la longueur maximale`() {
        //Arrange
        val nom = "b".repeat(ValidationRules.MAX_COURS_NOM_LENGTH)

        //Act
        val erreur = validator.validateNom(nom)

        //Assert
        assertNull(erreur)
    }
}
