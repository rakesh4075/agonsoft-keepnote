package com.keepnote.view.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.keepnote.R
import com.keepnote.databinding.ActivityWebviewBinding

class CommonWebview : AppCompatActivity() {
    lateinit var mBinding:ActivityWebviewBinding
    private var mDownx: Float = 0f
    private var webUrl: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_webview)
        mBinding.toolbar.toolbar.title = ""
        mBinding.toolbar.toolbartitle.text = "Privacy policy"
        setSupportActionBar(mBinding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initWebView()
        webUrl = "https://rakesh4075.github.io/agonsoft/privacy_policy.html"
        if (!(webUrl.isNullOrEmpty())) mBinding.notewebView.loadUrl(webUrl)
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
        mBinding.notewebView.setOnTouchListener { _, event ->
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

}
