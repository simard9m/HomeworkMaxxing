package com.example.homeworkmaxxing.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.homeworkmaxxing.data.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions LIMIT 1")
    fun observeSession(): Flow<Session?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: Session)

    @Query("DELETE FROM sessions")
    suspend fun deleteSession()
}
