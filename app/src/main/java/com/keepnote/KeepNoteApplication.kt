package com.keepnote

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.utils.NotificationAlarm
import com.keepnote.utils.RateThisApp




class KeepNoteApplication:Application() {

    companion object{
        private lateinit var instance: KeepNoteApplication
        fun getInstance(): KeepNoteApplication {
            if (!::instance.isInitialized) {
                instance = KeepNoteApplication()
            }

            return instance
        }


    }

    override fun onCreate() {
        super.onCreate()

        if (StoreSharedPrefData.INSTANCE.getPref("appstart",0,this) as Int!=1)
        StoreSharedPrefData.INSTANCE.savePrefValue("appstart",0,this)

        val config = RateThisApp.Config(3, 5)
        RateThisApp.init(config)
       // NotificationAlarm().setAlarm(this)


    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}