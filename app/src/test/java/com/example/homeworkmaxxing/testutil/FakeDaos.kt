package com.example.homeworkmaxxing.testutil

import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCoursDao(
    initialCours: List<Cours> = emptyList()
) : CoursDao {
    private val coursState = MutableStateFlow(initialCours.sortedBy { it.nom })

    override fun getAllCours(): Flow<List<Cours>> = coursState

    override suspend fun getAllCoursList(): List<Cours> = coursState.value

    override suspend fun getCoursById(id: Long): Cours? {
        return coursState.value.find { it.id == id }
    }

    override suspend fun insertCours(cours: Cours): Long {
        val nextId = if (cours.id == 0L) {
            (coursState.value.maxOfOrNull { it.id } ?: 0L) + 1L
        } else {
            cours.id
        }
        coursState.value = (coursState.value + cours.copy(id = nextId))
            .sortedBy { it.nom }
        return nextId
    }

    override suspend fun updateCours(cours: Cours) {
        coursState.value = coursState.value
            .map { if (it.id == cours.id) cours else it }
            .sortedBy { it.nom }
    }

    override suspend fun deleteCours(cours: Cours) {
        coursState.value = coursState.value.filterNot { it.id == cours.id }
    }

    override suspend fun countCours(): Int = coursState.value.size

    override suspend fun deleteAllCours() {
        coursState.value = emptyList()
    }

    override suspend fun insertAllCours(cours: List<Cours>): List<Long> {
        return cours.map { insertCours(it) }
    }
}

class FakeRoutineDao(
    initialRoutines: List<Routine> = emptyList()
) : RoutineDao {
    private val routinesState = MutableStateFlow(initialRoutines.sortedBy { it.date })

    override fun getAllRoutines(): Flow<List<Routine>> = routinesState

    override suspend fun getRoutineById(id: Int): Routine? {
        return routinesState.value.find { it.id == id }
    }

    override fun observeRoutineById(id: Int): Flow<Routine?> {
        return routinesState.map { routines -> routines.find { it.id == id } }
    }

    override suspend fun insertRoutine(routine: Routine): Long {
        val nextId = routine.id ?: ((routinesState.value.maxOfOrNull { it.id ?: 0 } ?: 0) + 1)
        routinesState.value = (routinesState.value + routine.copy(id = nextId))
            .sortedBy { it.date }
        return nextId.toLong()
    }

    override suspend fun updateRoutine(routine: Routine) {
        routinesState.value = routinesState.value
            .map { if (it.id == routine.id) routine else it }
            .sortedBy { it.date }
    }

    override suspend fun deleteRoutine(routine: Routine) {
        routinesState.value = routinesState.value.filterNot { it.id == routine.id }
    }

    override suspend fun deleteAllRoutines() {
        routinesState.value = emptyList()
    }

    override suspend fun countRoutines(): Int = routinesState.value.size

    override suspend fun insertRoutines(routines: List<Routine>): List<Long> {
        return routines.map { insertRoutine(it) }
    }
}
