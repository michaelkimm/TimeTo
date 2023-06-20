package com.tt.timeto.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.tt.timeto.util.TimeUtil

class AlertReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {

        var notificationHelper: NotificationHelper = NotificationHelper(context)

        // get data from intent
        var title = intent?.getStringExtra("title")
        var content = intent?.getStringExtra("content")
        var targetTime = intent?.getLongExtra("reservedTimeInMillis", 0)

        val localDateTime = TimeUtil.fromEpochMilliToLocalDateTime(targetTime!!)

        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(localDateTime.toString())

        // 푸시 알림 클릭 시 호출될 화면
        val activityIntent = Intent(context, AlarmActivity::class.java)

        // AlarmActivity에 전달할 데이터
        activityIntent.putExtra("title", title)
        activityIntent.putExtra("content", content)
        activityIntent.putExtra("timeLocalDateTimeString", localDateTime.toString())
        val pendingIntent =
            PendingIntent.getActivity(context, (System.currentTimeMillis() / 1000).toInt(), activityIntent, PendingIntent.FLAG_MUTABLE)
        nb.setContentIntent(pendingIntent)

        // 푸시 알림 호출
        notificationHelper.getManager().notify((System.currentTimeMillis() / 1000).toInt(), nb.build())
    }

}