package com.tt.timeto.notification

import androidx.room.TypeConverter
import com.google.gson.Gson
import kotlinx.datetime.LocalDate

class NotificationConverters {

    @TypeConverter
    fun jsonToLocalDate(value: String): Notification {
        return Gson().fromJson(value, Notification::class.java)
    }

    @TypeConverter
    fun localDateToJson(notification: Notification?): String? {
        return Gson().toJson(notification)
    }
}