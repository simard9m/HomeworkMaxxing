package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.model.Routine

class DeleteRoutineUseCase(private val routineDao: RoutineDao) {
    suspend operator fun invoke(routine: Routine) {
        routineDao.deleteRoutine(routine)
    }
}
