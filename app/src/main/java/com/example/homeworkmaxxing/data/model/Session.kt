package com.example.homeworkmaxxing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey
    val id: Int = 1,
    val dateFin: Long
)
