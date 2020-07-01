package com.keepnote.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.keepnote.R
import com.keepnote.view.homescreen.HomeScreen

class Splashscreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.splash)

        Handler().postDelayed({
            startActivity(Intent(this@Splashscreen, HomeScreen::class.java))
            finish()
        },100)
    }
}
