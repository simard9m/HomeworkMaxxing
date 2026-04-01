package com.example.homeworkmaxxing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cours")
data class Cours(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val nom: String,
    val couleurHex: Long
)
