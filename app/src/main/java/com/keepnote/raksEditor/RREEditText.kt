package com.keepnote.raksEditor

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.keepnote.R


class RREEditText: AppCompatEditText {

    private var mContext: Context? = null

    private var sToolbar: RRE_Toolbar? = null

    private var mToolbar: IRRE_Toolbar? = null

    private var sStylesList: ArrayList<IRRE_Style>? = ArrayList()

    private var mTextWatcher: TextWatcher? = null

    private val MONITORING = true

    constructor(context: Context):super(context,null)

    constructor(context: Context,attrs: AttributeSet):super(context,attrs,0){
        mContext = context as Activity
        sToolbar = RRE_Toolbar(context).getInstance()
        initGlobalValues()
        if (sToolbar != null) {
           sStylesList = sToolbar?.getStylesList()
        }
        init()
        setupListener()
    }

    constructor(context: Context, attr: AttributeSet, defStyle:Int):super(context,attr){

    }

    private fun init() {
        isFocusableInTouchMode = true
        if (mContext!=null)
        setBackgroundColor(ContextCompat.getColor(mContext!!,R.color.lightwindoebackground))
        inputType = (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
                or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
//        var padding = 8
//        padding = Util.getPixelByDp(mContext, padding)
//        setPadding(padding, padding, padding, padding)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.DEFAULT_FONT_SIZE);

    }

    private fun initGlobalValues() {
        val wh = mContext?.let { Util.getScreenWidthAndHeight(it) }
        if (wh!=null){
            Constants.SCREEN_WIDTH = wh[0]
            Constants.SCREEN_HEIGHT = wh[1]
        }

    }



    fun setToolbar(toolbar: RRE_Toolbar?) {
        sStylesList?.clear()
        sToolbar = toolbar
        sToolbar?.setEditText(this)
        val toolItems = toolbar?.getStylesList()
        if (toolItems != null) {
            for (toolItem in toolItems) {
                val style = toolItem
                sStylesList?.add(style)
            }
        }
    }
    private fun setupListener() {
        setupTextWatcher()
    }

    private fun setupTextWatcher() {

        var startPos = 0
        var endPos = 0
        mTextWatcher = object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (!MONITORING) {
                    return
                }
                Util.log("afterTextChanged:: s = $s")
                if (endPos <= startPos) {
                    Util.log("User deletes: start == $startPos endPos == $endPos")
                }
                if (sStylesList!=null){
                    for (style in sStylesList!!) {
                        style.applyStyle(s, startPos, endPos)
                    }
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!MONITORING) {
                    return
                }
                Util.log(
                    "beforeTextChanged:: s = " + s + ", start = " + start + ", count = " + count
                            + ", after = " + after
                )
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!MONITORING) {
                    return
                }

                Util.log(
                    "onTextChanged:: s = " + s + ", start = " + start + ", count = " + count + ", before = "
                            + before
                )
                startPos = start
                endPos = start + count
            }
        }
        addTextChangedListener(mTextWatcher)
    }

    fun getHtml(): String? {
        val html = StringBuffer()
        html.append("<html><body>")
        val editTextHtml = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.toHtml(
                editableText,
                Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL
            )
        } else {
            Html.toHtml(editableText)
        }
        html.append(editTextHtml)
        html.append("</body></html>")
        val htmlContent = html.toString()
            .replace(Constants.ZERO_WIDTH_SPACE_STR_ESCAPE.toRegex(), "")
        println(htmlContent)
        return htmlContent
    }
}
