package com.keepnote

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.keepnote.model.preferences.StoreSharedPrefData

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

        StoreSharedPrefData.INSTANCE.savePrefValue("appstart",true,this)

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}