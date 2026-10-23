package com.rifaapp.core.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun localDateToEpochDays(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    fun epochDaysToLocalDate(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }
}
