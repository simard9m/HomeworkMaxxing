package com.example.homeworkmaxxing.data.local

import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoomConvertersTest {

    private val converters = RoomConverters()

    @Test
    fun `la date locale doit survivre à un aller retour via epoch millis`() {
        //Arrange
        val value = LocalDateTime.of(2035, 5, 20, 16, 45)

        //Act
        val converted = converters.toLocalDateTime(converters.fromLocalDateTime(value))

        //Assert
        assertEquals(value, converted)
    }

    @Test
    fun `les valeurs enum doivent survivre à un aller retour via leur représentation texte`() {
        //Arrange et Act
        val repetabilite = converters.toRepetabilite(converters.fromRepetabilite(Repetabilite.MENSUEL))
        val priorite = converters.toPriorite(converters.fromPriorite(Priorite.URGENTE))
        val categorie = converters.toCategorieRoutine(
            converters.fromCategorieRoutine(CategorieRoutine.PROJET)
        )

        //Assert
        assertEquals(Repetabilite.MENSUEL, repetabilite)
        assertEquals(Priorite.URGENTE, priorite)
        assertEquals(CategorieRoutine.PROJET, categorie)
    }

    @Test
    fun `les valeurs null doivent rester null dans les convertisseurs`() {
        //Arrange, Act et Assert
        assertNull(converters.fromLocalDateTime(null))
        assertNull(converters.toLocalDateTime(null))
        assertNull(converters.fromRepetabilite(null))
        assertNull(converters.toRepetabilite(null))
        assertNull(converters.fromPriorite(null))
        assertNull(converters.toPriorite(null))
        assertNull(converters.fromCategorieRoutine(null))
        assertNull(converters.toCategorieRoutine(null))
    }
}
