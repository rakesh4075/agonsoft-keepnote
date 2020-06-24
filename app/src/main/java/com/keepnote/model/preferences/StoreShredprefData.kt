package com.keepnote.model.preferences

import android.content.Context
import android.content.SharedPreferences

class StoreSharedPrefData {
    private var editor: SharedPreferences.Editor? = null
    private var getShredprefValue: SharedPreferences? = null

    fun savePrefValue (key: String, value: Any?, context: Context) {
        if (editor == null)
            editor = context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).edit()
        deletePref(key)
               when (value) {
                is Boolean -> editor?.putBoolean(key, value)
                is Int -> editor?.putInt(key, value )
                is Float -> editor?.putFloat(key, value )
                is Long -> editor?.putLong(key, value )
                is String -> {
                    editor?.putString(key, value )
                    //Log.e("@@@@@1","$key$value")
                }
                is Enum<*> -> editor?.putString(key, value as String)
                else -> throw RuntimeException("Attempting to save non-primitive preference")
            }

            editor?.apply()
    }
            private fun deletePref(key: String) {
            editor?.remove(key)?.apply()

        }



           fun getPref(key: String, value: Any?, context: Context?): Any? {
               if (getShredprefValue == null)
                getShredprefValue = context?.applicationContext?.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
               return getShredprefValue?.all?.get(key) ?: value
        }
            fun removePref(key:String,context: Context?){
                editor?.remove(key)?.apply()
            }

    companion object {
        val INSTANCE: StoreSharedPrefData by lazy { StoreSharedPrefData() }
    }

}