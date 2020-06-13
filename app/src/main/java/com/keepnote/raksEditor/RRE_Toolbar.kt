package com.keepnote.raksEditor

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import com.keepnote.R


class RRE_Toolbar:LinearLayout {



    /**
     * Request code for selecting an image.
     */
    val REQ_IMAGE = 1

    /**
     * Request code for choosing a people to @.
     */
    val REQ_AT = 2

    /**
     * Request code for choosing a video.
     */
    val REQ_VIDEO_CHOOSE = 3

    /**
     * Request code for inserting a video
     */
    val REQ_VIDEO = 4

    private var mContext: Activity? = null

    private var mEditText: RREEditText? = null


    private var mLayoutDelay = 0
    private var mPreviousKeyboardHeight = 0
    private var mKeyboardShownNow = true
    private var mEmojiShownNow = false
    private var mHideEmojiWhenHideKeyboard = true
    private var mKeyboardHeight = 0
    private val mEmojiPanel: View? = null



    constructor(context: Context):super(context,null)

    constructor(context: Context,attrs:AttributeSet):super(context,attrs,0){
        mContext = context as Activity
        sInstance = this
        init()
    }

    constructor(context: Context,attr: AttributeSet,defStyle:Int):super(context,attr)


    /**
     * Supported styles list.
     */
    private val mStylesList = ArrayList<IRRE_Style>()

    /**
     * Bold Style
     */
    private var mBoldStyle: RRE_Bold? = null

    /**
     * Italic Style
     */
    private var mItalicStyle: RRE_Italic? = null


    /**
     * Underline Style
     */
    private var mUnderlineStyle: RRE_Underline? = null

    /**
     * Strikethrough Style
     */
    private var mStrikethroughStyle: RRE_Strikethrough? = null


    /**
     * Font color Style
     */
    private var mFontColorStyle: RRE_FontColor? = null


    /**
     * Font color Style
     */
    private var mTextLinkStyle: RRE_Link? = null

    /**
     * Bold button.
     */
    private var mBoldImageView: ImageView? = null

    /**
     * Italic button.
     */
    private var mItalicImageView: ImageView? = null

    /**
     * Underline button.
     */
    private var mUnderlineImageView: ImageView? = null

    /**
     * Strikethrough button.
     */
    private var mStrikethroughImageView: ImageView? = null


    /**
     * Foreground color image view.
     */
    private var mFontColorImageView: ImageView? = null



    /**
     * Background button.
     */
    private var mBackgroundImageView: ImageView? = null


    /**
     * Text Link button.
     */
    private var mTextLinkImageView: ImageView? = null

    var sInstance: RRE_Toolbar? = null

    /**
     * The color palette.
     */


    /**
     * Background color Style
     */
    private var mBackgroundColorColoStyle: RRE_BackgroundColor? = null


    private fun init() {
        val layoutInflater = LayoutInflater.from(mContext)
        layoutInflater.inflate(getLayoutId(), this, true)
        this.orientation = VERTICAL
        initViews()
        initStyles()
        initKeyboard()

    }


    fun setEditText(editText: RREEditText?) {
        mEditText = editText
        bindToolbar()
    }

    private fun bindToolbar() {
        mBoldStyle?.setEditText(mEditText)
        mItalicStyle?.setEditText(mEditText)
        mStrikethroughStyle?.setEditText(mEditText)
        mUnderlineStyle?.setEditText(mEditText)
        mFontColorStyle?.setEditText(mEditText)
        mBackgroundColorColoStyle?.setEditText(mEditText)
        mTextLinkStyle?.setEditText(mEditText)

    }


    private fun initKeyboard() {
        val window = mContext?.window
        val rootView = window?.decorView?.findViewById<View>(android.R.id.content)
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (mLayoutDelay == 0) {
                        init()
                        return
                    }
                    rootView.postDelayed({ init() }, mLayoutDelay.toLong())
                }

                private fun init() {
                    val r = Rect()
                    val view = window.decorView
                    view.getWindowVisibleDisplayFrame(r)
                    val screenWandH = mContext?.let { Util.getScreenWidthAndHeight(it) }
                    val screenHeight = screenWandH?.get(1)
                    val keyboardHeight = screenHeight?.minus(r.bottom)
                    if (mPreviousKeyboardHeight != keyboardHeight) {
                        if (keyboardHeight != null) {
                            if (keyboardHeight > 100) {
                                if (keyboardHeight != null) {
                                    mKeyboardHeight = keyboardHeight
                                }
                                onKeyboardShow()
                            } else {
                                onKeyboardHide()
                            }
                        }
                    }
                    if (keyboardHeight != null) {
                        mPreviousKeyboardHeight = keyboardHeight
                    }
                }
            })
    }

    private fun onKeyboardHide() {
        mKeyboardShownNow = true
        toggleEmojiPanel(false)
        mEmojiShownNow = false
        mLayoutDelay = 100
    }

    private fun onKeyboardShow() {
        mKeyboardShownNow = false
        if (mHideEmojiWhenHideKeyboard) {
            toggleEmojiPanel(false)
        } else {
            postDelayed({ mHideEmojiWhenHideKeyboard = true }, 100)
        }
    }

    private fun toggleEmojiPanel(b: Boolean) {

    }

    private fun initStyles() {
        mBoldStyle = mBoldImageView?.let { mContext?.let { it1 -> RRE_Bold(it1, it) } }
        mItalicStyle = mItalicImageView?.let { mContext?.let { it1 -> RRE_Italic(it1, it) } }
        mStrikethroughStyle = mStrikethroughImageView?.let { mContext?.let { it1 -> RRE_Strikethrough(it1, it) } }
        mUnderlineStyle = mUnderlineImageView?.let { mContext?.let { it1 -> RRE_Underline(it1, it) } }
        mFontColorStyle = mFontColorImageView?.let { mContext?.let { it1 -> RRE_FontColor(it1, it) } }
        mBackgroundColorColoStyle = mBackgroundImageView?.let { mContext?.let { it1 -> RRE_BackgroundColor(it1, it,Color.YELLOW) } }
        mTextLinkStyle = mTextLinkImageView?.let { mContext?.let { it1 -> RRE_Link(it1, it) } }

        mBoldStyle?.let { mStylesList.add(it) }
        mItalicStyle?.let { mStylesList.add(it) }
        mStrikethroughStyle?.let { mStylesList.add(it) }
        mUnderlineStyle?.let { mStylesList.add(it) }
        mFontColorStyle?.let { mStylesList.add(it) }
        mBackgroundColorColoStyle?.let { mStylesList.add(it) }
        mTextLinkStyle?.let { mStylesList.add(it) }
    }

    private fun initViews() {
        mBoldImageView = findViewById(R.id.rteBold)
        mItalicImageView = findViewById(R.id.rteItalic)
        mStrikethroughImageView= findViewById(R.id.rteStrikethrough)
        mUnderlineImageView= findViewById(R.id.rteUnderline)

      //  mcolorPallete = mContext?.findViewById(R.id.rteColorPalette)
        mFontColorImageView = findViewById(R.id.rteFontColor)
        mBackgroundImageView = findViewById(R.id.rteBackground)
        mTextLinkImageView = findViewById(R.id.rteLink)
        
    }

    private fun getLayoutId(): Int {
        return R.layout.rre_toolbar
    }

    fun getInstance(): RRE_Toolbar? {
        return RRE_Toolbar(context).sInstance
    }


    fun getEditText(): RREEditText? {
        return mEditText
    }

    fun addToolItem(toolItem: IRRE_ToolItem?) {
        val view: View? =  toolItem?.getView(context)
        this.addView(view)
        // addView to toolbar
        // add tool item to a collection
    }

    fun getStylesList(): ArrayList<IRRE_Style> {
        return mStylesList
    }

    fun setColorPaletteColor(color: Int) {
      //  mcolorPallete?.setColor(color)
    }

}