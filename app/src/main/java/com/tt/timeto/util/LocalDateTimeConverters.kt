package com.tt.timeto.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverters {

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun jsonToLocalDate(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun localDateToJson(localDateTime: LocalDateTime?): String? {
        return localDateTime.toString()
    }
}