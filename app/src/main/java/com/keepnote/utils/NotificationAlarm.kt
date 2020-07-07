package com.keepnote.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import java.util.*


class NotificationAlarm {

        fun setAlarm(context: Context) {
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 14)
                set(Calendar.MINUTE, 20)
            }
            if (calendar.time.compareTo(Date()) < 0) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )


        }


}