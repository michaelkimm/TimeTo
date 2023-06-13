package com.tt.timeto.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tt.timeto.notification.NotificationHelper

class AlertReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        var notificationHelper: NotificationHelper = NotificationHelper(context)

        // get data from intent
        var time = intent?.extras?.getString("time")

        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(time)

        // 알림 호출
        notificationHelper.getManager().notify(1, nb.build())
    }
}