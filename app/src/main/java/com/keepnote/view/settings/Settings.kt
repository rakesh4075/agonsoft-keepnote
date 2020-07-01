package com.keepnote.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepnote.view.homescreen.HomeScreen
import com.keepnote.R
import com.keepnote.databinding.ActivitySettingsBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.utils.Constants

class Settings : AppCompatActivity() {

    private var showtoolbarView: Boolean = false
    private lateinit var mbinding:ActivitySettingsBinding
    private lateinit var toolbar: Toolbar
    private lateinit var settingAdapter:SettingsRecyclerAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean){
            setTheme(R.style.DarkTheme)
            showtoolbarView = true
        }
        else
            setTheme(R.style.LightTheme)

        mbinding = DataBindingUtil.setContentView(this,R.layout.activity_settings)
      // overridePendingTransition(R.anim.left_slide_in,R.anim.left_slide_out)
        toolbar = mbinding.toolbarll.toolbar
        toolbar.title=""
        mbinding.toolbarll.toolbartitle.text = getString(R.string.settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        if (showtoolbarView)  mbinding.toolbarll.view.visibility = View.GONE
        layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        settingAdapter = SettingsRecyclerAdapter()
        mbinding.settingRecyler.layoutManager = layoutManager
        mbinding.settingRecyler.adapter = settingAdapter
        mbinding.adView.let { Constants.showBottomAds(this,it) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this, HomeScreen::class.java))
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }
}
