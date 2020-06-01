package com.keepnote

import android.app.Application
import android.content.Context
import com.keepnote.model.preferences.StoreSharedPrefData

class KeepNoteApplication:Application() {

    companion object{
        val Context.KeepNoteApplication:KeepNoteApplication
        get() = applicationContext as KeepNoteApplication
    }

    override fun onCreate() {
        super.onCreate()

        StoreSharedPrefData.INSTANCE.savePrefValue("appstart",true,this)

    }



}