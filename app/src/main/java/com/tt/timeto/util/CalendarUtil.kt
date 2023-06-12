package com.tt.timeto.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.Calendar

class CalendarUtil {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        var selectedDate: Calendar = Calendar.getInstance()
    }
}