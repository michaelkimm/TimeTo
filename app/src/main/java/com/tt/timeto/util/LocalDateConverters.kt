package com.tt.timeto.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverters {

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun jsonToLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun localDateToJson(localDate: LocalDate?): String? {
        return localDate.toString()
    }
}