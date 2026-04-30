package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.model.Routine

class UpdateRoutineUseCase(private val routineDao: RoutineDao) {
    suspend operator fun invoke(routine: Routine) {
        routineDao.updateRoutine(routine)
    }
}
