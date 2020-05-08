package com.keepnote.view.noteview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.keepnote.EditNote
import com.keepnote.R
import com.keepnote.databinding.NotedetViewBinding
import com.keepnote.notesDB.NoteViewmodel
import com.keepnote.notesDB.NoteViewmodelFactory
import com.raks.roomdatabase.NoteDatabase

class NoteWebview : AppCompatActivity() {
    private var notecolor: Int?=null
    private var noteid: Long?=null
    private var notetitle: String?=null
    private var m_downX: Float = 0f
    private lateinit var mBinding: NotedetViewBinding
    private var from: Int? = 0
    private lateinit var viewmodel: NoteViewmodel
    private var notecontent:String?=""
    private var webUrl: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if (!(webUrl.isNullOrEmpty())) mBinding.notewebView.loadDataWithBaseURL(null,webUrl,"text/html", "utf-8", null);
        notecolor?.let {colorcode->
            if ((colorcode.toString().subSequence(0,1) as String) == "-"){
                mBinding.notewebView.setBackgroundColor(colorcode)
            }else{
                mBinding.notewebView.setBackgroundColor(ContextCompat.getColor(this, colorcode))
            }
        }
        notetitle?.let {
            if (it.isNotEmpty())
            mBinding.toolbar.toolbartitle.text = it
            else
                mBinding.toolbar.toolbartitle.text = "<Untitled>"
            mBinding.toolbar.toolbarEditicon.visibility = View.VISIBLE
        }
        setSupportActionBar(mBinding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.toolbar.toolbarEditicon.setOnClickListener {
            val editNoteIntent = Intent(this, EditNote::class.java)
            if (notetitle!=null && notecontent!=null && notecolor!=null){
                editNoteIntent.putExtra("noteid",noteid)
                editNoteIntent.putExtra("from",0)
                startActivity(editNoteIntent)
                finish()
            }
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
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view?.getContext()?.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                    return true
                } else if (url!!.startsWith("mailto:")) {
                    val i = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(i)
                    return true
                } else {
                    return false
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
                Log.d("@@@@@@error",error.toString())
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
                    m_downX = event.x
                }
                MotionEvent.ACTION_DOWN ->{
                    // set x so that it doesn't move
                    event.setLocation(m_downX, event.y)
                }
            }
            return@setOnTouchListener false
        }


    }
    internal class MyWebChromeClient : WebChromeClient() {

    }

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



