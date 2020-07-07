package com.keepnote.view


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.keepnote.utils.NotificationHelper
import com.keepnote.view.homescreen.HomeScreen

class Splashscreen : AppCompatActivity() {
var notification_id:Int?=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notification_id = intent.getIntExtra("notification_id",0)
        if (notification_id!=null && notification_id==1){
            val notificationHelper = NotificationHelper()
            notificationHelper.removeNotification(this, notification_id!!)
        }


        Handler().postDelayed({
            startActivity(Intent(this@Splashscreen, HomeScreen::class.java))
            finish()
        },100)
    }
}
