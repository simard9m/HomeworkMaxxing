package com.example.homeworkmaxxing.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.homeworkmaxxing.data.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutineDao {

    @Dao
    interface RoutineDao {
        @Query("SELECT * FROM routines")
        fun getRoutines(): Flow<List<Routine>>

        @Query("SELECT * FROM routines WHERE id = :id")
        fun getRoutine(id:Int) : Routine?

        @Upsert
        suspend fun upsertRoutine(routine: Routine)
        @Delete
        suspend fun deleteRoutine(routine: Routine)

    }
}