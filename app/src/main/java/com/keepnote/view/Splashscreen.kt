package com.keepnote.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.keepnote.HomeScreen
import com.keepnote.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splashscreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        GlobalScope.launch {
            delay(1000)
            startActivity(Intent(this@Splashscreen, HomeScreen::class.java))
            finish()
        }
    }
}
