package com.keepnote.view.noteview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.keepnote.EditNote
import com.keepnote.R
import com.keepnote.databinding.NotedetViewBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.NoteDatabase
import com.keepnote.notesDB.NoteViewmodel
import com.keepnote.notesDB.NoteViewmodelFactory

class NoteWebview : AppCompatActivity() {
    private var notecolor: Int?=null
    private var noteid: Long?=null
    private var notetitle: String?=null
    private var mDownx: Float = 0f
    private lateinit var mBinding: NotedetViewBinding
    private var from: Int? = 0
    private lateinit var viewmodel: NoteViewmodel
    private var notecontent:String?=""
    private var webUrl: String? = ""
    private var isDarktheme = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarktheme =
            if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean) {
                setTheme(R.style.DarkTheme)
                true
            } else{
                setTheme(R.style.LightTheme)
                false
            }
        mBinding = DataBindingUtil.setContentView(this,R.layout.notedet_view)
        mBinding.toolbar.toolbar.title = ""
        initWebView()


        val application = requireNotNull(this).application
        val dataSource = NoteDatabase.invoke(this).getNoteDao()
        val noteViewmodelFactory = NoteViewmodelFactory(dataSource,application)
        viewmodel = ViewModelProviders.of(this,noteViewmodelFactory).get(NoteViewmodel::class.java)
        mBinding.noteviewmodel = viewmodel
        mBinding.lifecycleOwner = this

        intent?.let {
            notecontent = intent.getStringExtra("content")
            notetitle = intent.getStringExtra("title")
            noteid = intent.getLongExtra("noteid",7)
            notecolor =  intent.getIntExtra("colorcode",0)
            from = intent.getIntExtra("from",0)
            from?.let {
                webUrl = getWebUrl(from!!)

            }
        }
        if (!(webUrl.isNullOrEmpty())) mBinding.notewebView.loadDataWithBaseURL(null,webUrl,"text/html", "utf-8", null)
        notecolor?.let {colorcode->
            if ((colorcode.toString().subSequence(0,1) as String) == "-"){
                mBinding.notewebView.setBackgroundColor(colorcode)
            }else{
                mBinding.notewebView.setBackgroundColor(if (isDarktheme) ContextCompat.getColor(this,R.color.text_lt_clr) else ContextCompat.getColor(this,R.color.lightprimary))
            }
        }
        notetitle?.let {title->
            when {
                title.isEmpty() -> {
                    mBinding.toolbar.toolbartitle.text = getString(R.string.title_empty_text)
                }
                title.length>=20 -> mBinding.toolbar.toolbartitle.text = getString(R.string.note_title,title.subSequence(0,25))
                else ->mBinding.toolbar.toolbartitle.text = title
            }
        }
        setSupportActionBar(mBinding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        fun loadEditnote(){
            val editNoteIntent = Intent(this, EditNote::class.java)
            if (notetitle!=null && notecontent!=null && notecolor!=null){
                editNoteIntent.putExtra("noteid",noteid)
                editNoteIntent.putExtra("from",0)
                startActivity(editNoteIntent)
                finish()
            }
        }
        val gestureDetector = GestureDetector(this,object :GestureDetector.SimpleOnGestureListener(){
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                loadEditnote()
                return true
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                loadEditnote()
                return true
            }
        })
        mBinding.notewebView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
        }

    }

    private fun getWebUrl(getUrlFor: Int): String? {
        when (getUrlFor) {
            1 -> {
                return if (notecontent!=null) {
                    notecontent
                } else "null"

            }
        }
        return ""
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun initWebView() {
        mBinding.notewebView.webChromeClient = MyWebChromeClient()
        mBinding.notewebView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mBinding.run {
                    progressBar.visibility = View.GONE
                    notewebView.visibility = View.VISIBLE
                }
                invalidateOptionsMenu()
            }


            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view?.context?.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                    true
                } else if (url!!.startsWith("mailto:")) {
                    val i = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(i)
                    true
                } else {
                    false
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mBinding.progressBar.visibility = View.GONE
                mBinding.notewebView.visibility = View.VISIBLE
                invalidateOptionsMenu()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                mBinding.progressBar.visibility = View.GONE
                invalidateOptionsMenu()

            }

        }
        mBinding.notewebView.clearCache(true)
        mBinding.notewebView.clearHistory()
        mBinding.notewebView.settings.userAgentString = "Android"
        mBinding.notewebView.settings.javaScriptEnabled = true
        mBinding.notewebView.isHorizontalScrollBarEnabled = false
        mBinding.notewebView.setOnTouchListener { view, event ->
            if (event.pointerCount>1){
                //Multi touch detected
                return@setOnTouchListener true
            }
            when(event.action){
                MotionEvent.ACTION_UP ->{
                    // save the x
                    mDownx = event.x
                }
                MotionEvent.ACTION_DOWN ->{
                    // set x so that it doesn't move
                    event.setLocation(mDownx, event.y)
                }
            }
            return@setOnTouchListener false
        }


    }
    internal class MyWebChromeClient : WebChromeClient()

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

        return true
    }

    }



