package com.tt.timeto.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.tt.timeto.MainActivity
import com.tt.timeto.dayplan.DayPlanActivity
import com.tt.timeto.dayplan.UpdateActivity
import com.tt.timeto.notification.NotificationHelper
import java.text.DateFormat
import java.util.Calendar

class AlertReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        var notificationHelper: NotificationHelper = NotificationHelper(context)

        // get data from intent
        var title = intent?.getStringExtra("title")
        var content = intent?.getStringExtra("content")
        var targetTime = intent?.getStringExtra("time")

        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(targetTime)

        // 푸시 알림 클릭 시 호출될 화면
        val activityIntent = Intent(context, AlarmActivity::class.java)

        // AlarmActivity에 전달할 데이터
        activityIntent.putExtra("title", title)
        activityIntent.putExtra("content", content)
        activityIntent.putExtra("time", targetTime)
        val pendingIntent =
            PendingIntent.getActivity(context, (System.currentTimeMillis() / 10000).toInt(), activityIntent, PendingIntent.FLAG_MUTABLE)
        nb.setContentIntent(pendingIntent)

        // 푸시 알림 호출
        notificationHelper.getManager().notify(1, nb.build())
    }

}