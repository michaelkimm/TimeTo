package com.tt.timeto.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

class TimeUtil {

    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        val zoneId: ZoneId = ZoneId.systemDefault()

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromLocalDateToEpochMilli(localDate: LocalDate): Long {
            return localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromLocalDateTimeToEpochMilli(localDateTime: LocalDateTime): Long {
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromEpochMilliToLocalDate(epochMilli: Long): LocalDate {
            val localDateTime = fromEpochMilliToLocalDateTime(epochMilli)
            return localDateTime.toLocalDate()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromEpochMilliToLocalDateTime(epochMilli: Long): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zoneId)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromCalendarToLocalDate(calendar: Calendar): LocalDate? {
            return calendar?.time?.toInstant()?.atZone(zoneId)?.toLocalDate();
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromCalendarToLocalDateTime(calendar: Calendar): LocalDateTime? {
            return calendar?.time?.toInstant()?.atZone(zoneId)?.toLocalDateTime();
        }
    }
}