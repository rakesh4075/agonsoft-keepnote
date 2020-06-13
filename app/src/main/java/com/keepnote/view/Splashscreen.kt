package com.keepnote.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.keepnote.HomeScreen
import com.keepnote.R
import com.keepnote.model.preferences.StoreSharedPrefData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splashscreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean)
//            setTheme(R.style.DarkTheme)
//        else
//            setTheme(R.style.LightTheme)

        setContentView(R.layout.activity_splashscreen)
        GlobalScope.launch {
            delay(150)
            startActivity(Intent(this@Splashscreen, HomeScreen::class.java))
            finish()
        }
    }
}
