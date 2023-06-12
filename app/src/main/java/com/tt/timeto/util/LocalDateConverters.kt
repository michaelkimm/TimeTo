package com.tt.timeto.util

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class LocalDateConverters {

    @TypeConverter
    fun jsonToLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun localDateToJson(localDate: LocalDate?): String? {
        return localDate.toString()
    }
}