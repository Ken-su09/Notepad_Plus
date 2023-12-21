package com.suonk.notepad_plus.utils

import androidx.room.TypeConverter
import com.suonk.notepad_plus.domain.ColorEntity
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

    @TypeConverter
    fun fromIntToColorEntity(color: Int): ColorEntity = ColorEntity.values()[color]

    @TypeConverter
    fun fromColorEntityToInt(colorEntity: ColorEntity) = colorEntity.ordinal
}