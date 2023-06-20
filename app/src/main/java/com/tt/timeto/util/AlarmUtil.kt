package com.tt.timeto.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.tt.timeto.AppDatabase
import com.tt.timeto.dayplan.ToDo
import com.tt.timeto.notification.AlertReceiver
import com.tt.timeto.notification.Notification
import java.time.LocalDateTime
import java.util.Calendar

class AlarmUtil {

    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        public fun updateOrInsertAlarm(
            context: Context,
            reservedAlarm: Calendar?,
            notificationId: Long?,
            toDoRowId: Long?
        ): Long? {
            if (reservedAlarm == null) {
                return null
            }
            // 알람 수정
            var notification: Notification = Notification(
                notificationId,
                TimeUtil.fromCalendarToLocalDateTime(reservedAlarm!!),
                toDoRowId
            )
            var db: AppDatabase? = AppDatabase.getDatabase(context)

            if (notificationId == null) {
                return db?.notificationDao()?.insertNotification(notification)
            } else {
                db?.notificationDao()?.updateNotification(notification)
                return notificationId
            }
        }

        public fun cancelAlarmByManager(
            context: Context,
            alarmManager: AlarmManager,
            notification: Notification?
        ) {
            if (notification == null)
                return

            // 알람매니저 선언

            var intent = Intent(context, AlertReceiver::class.java)

            var pendingIntent = PendingIntent.getBroadcast(
                context,
                notification.notification_id!!.toInt(),
                intent,
                0 or PendingIntent.FLAG_MUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        public fun startAlarm(
            toDoRowId: Long?,
            notificationId: Long?,
            systemService: AlarmManager,
            context: Context
        ) {

            if (notificationId == null)
                return

            // 알림 찾기
            var db: AppDatabase? = AppDatabase.getDatabase(context)
            val notification: Notification? =
                db?.notificationDao()?.getNotification(notificationId!!)

            if (notification == null || notification.reservedTime == null)
                return

            // 투두 찾기
            val toDo: ToDo? = db?.toDoDao()?.getToDo(toDoRowId!!.toInt())
            if (toDo == null)
                return

            var c: Calendar = Calendar.getInstance()
            c.timeInMillis = TimeUtil.fromLocalDateTimeToEpochMilli(notification.reservedTime)

            // 알람매니저 선언
            var alarmManager: AlarmManager = systemService as AlarmManager

            var intent = Intent(context, AlertReceiver::class.java)

            intent.putExtra(
                "reservedTimeInMillis",
                TimeUtil.fromLocalDateTimeToEpochMilli(notification.reservedTime)
            )

            var pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId.toInt(),
                intent,
                0 or PendingIntent.FLAG_MUTABLE
            )

            // 설정 시간이 현재 시간 이전이면 +1일
            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, 1)
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
        }
    }
}