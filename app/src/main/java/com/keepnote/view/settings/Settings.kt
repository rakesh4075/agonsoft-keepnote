package com.keepnote.view.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepnote.HomeScreen
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.ActivitySettingsBinding
import com.keepnote.databinding.HomescreenBindingImpl
import com.keepnote.model.preferences.StoreSharedPrefData

class Settings : AppCompatActivity() {

    private var showtoolbarView: Boolean = false
    lateinit var mbinding:ActivitySettingsBinding
    private lateinit var toolbar: Toolbar
    lateinit var settingAdapter:SettingsRecyclerAdapter
    lateinit var layoutManager: LinearLayoutManager

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
        if (showtoolbarView)  mbinding.toolbarll.vw1.visibility = View.GONE
        layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        settingAdapter = SettingsRecyclerAdapter(object :NoteListAdapter.NotesListner{
            override fun takeActionForNotes(actionFor: String, noteId: Long, position: Int) {

            }
        })
        mbinding.settingRecyler.layoutManager = layoutManager
        mbinding.settingRecyler.adapter = settingAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this,HomeScreen::class.java))



    }
}
