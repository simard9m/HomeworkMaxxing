package com.example.homeworkmaxxing.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.homeworkmaxxing.data.model.Cours
import kotlinx.coroutines.flow.Flow

@Dao
interface CoursDao {

    @Query("SELECT * FROM cours ORDER BY nom ASC")
    fun getAllCours(): Flow<List<Cours>>

    @Query("SELECT * FROM cours ORDER BY nom ASC")
    suspend fun getAllCoursList(): List<Cours>

    @Query("SELECT * FROM cours WHERE id = :id LIMIT 1")
    suspend fun getCoursById(id: Long): Cours?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCours(cours: Cours): Long

    @Update
    suspend fun updateCours(cours: Cours)

    @Delete
    suspend fun deleteCours(cours: Cours)

    @Query("SELECT COUNT(*) FROM cours")
    suspend fun countCours(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCours(cours: List<Cours>): List<Long>
}
