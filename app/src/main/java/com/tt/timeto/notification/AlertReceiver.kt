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

        Log.d("kkang", "alarm!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        var notificationHelper: NotificationHelper = NotificationHelper(context)

        // get data from intent
        var time = intent?.extras?.getString("time")

        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(time)

        val intent = Intent(context, AlarmActivity::class.java)
        intent.putExtra("time", time)
        val pendingIntent =
            PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_MUTABLE)
        nb.setContentIntent(pendingIntent)

        // 알림 호출
        notificationHelper.getManager().notify(1, nb.build())
    }
}