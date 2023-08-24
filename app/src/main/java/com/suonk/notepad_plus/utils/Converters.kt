package com.suonk.notepad_plus.utils

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun fromLocalDateToLong(value: LocalDateTime?): Long? = value?.toEpochSecond(ZoneOffset.UTC)

    @TypeConverter
    fun fromLongToLocalDate(value: Long?): LocalDateTime? = value?.let {
        Instant.ofEpochSecond(it).atZone(ZoneOffset.UTC).toLocalDateTime()
    }
}