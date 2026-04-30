package com.example.homeworkmaxxing.domain.usecase

import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.model.Cours

class AddCoursUseCase(private val coursDao: CoursDao) {
    suspend operator fun invoke(cours: Cours): Long {
        return coursDao.insertCours(cours)
    }
}
