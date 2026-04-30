package com.example.homeworkmaxxing.domain.validation

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.util.ValidationRules
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoutineValidatorTest {

    private val validator = RoutineValidator()

    @Test
    fun `la validation doit réussir quand tous les champs de la routine sont valides`() {
        //Arrange
        val nom = "Remise du travail"
        val description = "Terminer les derniers ajustements"
        val date = LocalDateTime.of(2035, 5, 10, 14, 0)

        //Act
        val erreur = validator.validate(
            nom = nom,
            description = description,
            date = date,
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertNull(erreur)
    }

    @Test
    fun `la validation doit échouer quand le nom de la routine est vide`() {
        //Arrange
        val nom = ""

        //Act
        val erreur = validator.validate(
            nom = nom,
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("Le nom est requis.", erreur)
    }

    @Test
    fun `la validation doit échouer quand le nom de la routine dépasse la longueur maximale`() {
        //Arrange
        val nom = "a".repeat(ValidationRules.MAX_ROUTINE_NOM_LENGTH + 1)

        //Act
        val erreur = validator.validate(
            nom = nom,
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("Le nom de la routine est trop long.", erreur)
    }

    @Test
    fun `la validation doit échouer quand la description dépasse la longueur maximale`() {
        //Arrange
        val description = "d".repeat(ValidationRules.MAX_ROUTINE_DESCRIPTION_LENGTH + 1)

        //Act
        val erreur = validator.validate(
            nom = "Nom valide",
            description = description,
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("La description est trop longue.", erreur)
    }

    @Test
    fun `la validation doit échouer quand la date est nulle`() {
        //Arrange
        val date = null

        //Act
        val erreur = validator.validate(
            nom = "Nom valide",
            description = "Description",
            date = date,
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("La date et l'heure sont requises.", erreur)
    }

    @Test
    fun `la validation doit échouer quand la date est dans le passé lors d une création`() {
        //Arrange
        val date = LocalDateTime.of(2035, 5, 1, 7, 0)

        //Act
        val erreur = validator.validate(
            nom = "Nom valide",
            description = "Description",
            date = date,
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("La date et l'heure ne peuvent pas être dans le passé.", erreur)
    }

    @Test
    fun `la validation doit échouer quand la date dépasse la fin de session`() {
        //Arrange
        val date = LocalDateTime.of(2035, 6, 1, 10, 0)

        //Act
        val erreur = validator.validate(
            nom = "Nom valide",
            description = "Description",
            date = date,
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("La date ne peut pas dépasser la date de fin de session.", erreur)
    }

    @Test
    fun `la validation doit échouer quand la catégorie est nulle`() {
        //Arrange
        val categorie = null

        //Act
        val erreur = validator.validate(
            nom = "Nom valide",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = categorie,
            priorite = Priorite.HAUTE,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("La catégorie est requise.", erreur)
    }

    @Test
    fun `la validation doit échouer quand la priorité est nulle`() {
        //Arrange
        val priorite = null

        //Act
        val erreur = validator.validate(
            nom = "Nom valide",
            description = "Description",
            date = LocalDateTime.of(2035, 5, 10, 14, 0),
            sessionLastDay = LocalDate.of(2035, 5, 31),
            isEditMode = false,
            categorie = CategorieRoutine.DEVOIR,
            priorite = priorite,
            now = LocalDateTime.of(2035, 5, 1, 8, 0)
        )

        //Assert
        assertEquals("La priorité est requise.", erreur)
    }
}
