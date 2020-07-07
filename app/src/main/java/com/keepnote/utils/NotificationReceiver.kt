package com.keepnote.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val noteCounts = Constants.TOTALNOTECOUNTS
        val notificationMsg = "You have ${noteCounts} notes, Review It!"
        if (noteCounts!=null && noteCounts>0){
            if (context != null) {
                val notificationHelper = NotificationHelper()
                notificationHelper.createNotification("Reminder",notificationMsg,context)
            }
        }

    }

}
