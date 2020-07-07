package com.keepnote.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.keepnote.R
import com.keepnote.view.Splashscreen

class NotificationHelper {
    private var largeIcon: Bitmap?=null
    private val NOTIFICATION_CHANNEL_ID = "10001"

    fun createNotification(title: String?, notificationMsg: String?,context: Context) {
            val intent = Intent(context, Splashscreen::class.java)
            intent.putExtra("notification_id", 1)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val resultPendingIntent = PendingIntent.getActivity(
                context,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val mBuilder = context.let { NotificationCompat.Builder(it, NOTIFICATION_CHANNEL_ID) }


           largeIcon =  BitmapFactory.decodeResource(context.getResources(), R.drawable.note_logo)
              mBuilder.setSmallIcon(R.drawable.note_logo)
                      ?.setLargeIcon(largeIcon)
                      ?.setContentTitle(title)
                      ?.setContentText(notificationMsg)
                      ?.setAutoCancel(false)
                      ?.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                      ?.setContentIntent(resultPendingIntent)
        mBuilder.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(BitmapFactory.decodeResource(context.resources, R.drawable.noteimg))
                .bigLargeIcon(largeIcon)
        )
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Communication",
                    importance
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
               if (mNotificationManager!=null)
                   mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }

            mNotificationManager?.notify(1, mBuilder.build())
        }
    fun removeNotification(context: Context, id: Int) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id)
    }

}
