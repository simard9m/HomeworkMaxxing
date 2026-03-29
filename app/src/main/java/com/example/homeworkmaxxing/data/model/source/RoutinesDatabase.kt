package com.example.homeworkmaxxing.data.model.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.data.model.RoutineDao

@Database(entities = [Routine::class], version = 1)
abstract class RoutinesDatabase : RoomDatabase() {

    abstract val dao: RoutineDao
    companion object {
        const val DATABASE_NAME = "routines_db"
    }
}