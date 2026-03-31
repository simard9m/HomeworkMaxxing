package com.example.homeworkmaxxing.data.local

import androidx.room.TypeConverter
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class RoomConverters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? {
        return value?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        }
    }

    @TypeConverter
    fun fromRepetabilite(value: Repetabilite?): String? = value?.name

    @TypeConverter
    fun toRepetabilite(value: String?): Repetabilite? {
        return value?.let { Repetabilite.valueOf(it) }
    }

    @TypeConverter
    fun fromPriorite(value: Priorite?): String? = value?.name

    @TypeConverter
    fun toPriorite(value: String?): Priorite? {
        return value?.let { Priorite.valueOf(it) }
    }

    @TypeConverter
    fun fromCategorieRoutine(value: CategorieRoutine?): String? = value?.name

    @TypeConverter
    fun toCategorieRoutine(value: String?): CategorieRoutine? {
        return value?.let { CategorieRoutine.valueOf(it) }
    }
}
