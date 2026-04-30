package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.model.Cours

class DeleteCoursUseCase(private val coursDao: CoursDao) {
    suspend operator fun invoke(cours: Cours) {
        coursDao.deleteCours(cours)
    }
}
