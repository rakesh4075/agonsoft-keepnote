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
import com.keepnote.EditNote
import com.keepnote.R
import com.keepnote.utils.ColorPickerListener
import com.keepnote.utils.ColorPickerView


class RRE_Toolbar:LinearLayout {


    private var mContext: Activity? = null

    private var mEditText: RREEditText? = null


    private var mLayoutDelay = 0
    private var mPreviousKeyboardHeight = 0
    private var mKeyboardShownNow = true
    private var mEmojiShownNow = false
    private var mHideEmojiWhenHideKeyboard = true
    private var mKeyboardHeight = 0




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
    private val mStylesList = ArrayList<IRREStyle>()

    /**
     * Bold Style
     */
    private var mBoldStyle: RREBold? = null

    /**
     * Italic Style
     */
    private var mItalicStyle: RREItalic? = null


    /**
     * Underline Style
     */
    private var mUnderlineStyle: RREUnderline? = null

    /**
     * Strikethrough Style
     */
    private var mStrikethroughStyle: RREStrikethrough? = null

    /**
     * The color palette.
     */
    private var mColorPalette: ColorPickerView? = null

    /**
     * Font color Style
     */
    private var mFontColorStyle: RREFontColor? = null


    /**
     * Font color Style
     */
    private var mTextLinkStyle: RRELink? = null

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

    private var sInstance: RRE_Toolbar? = null

    /**
     * The color palette.
     */


    /**
     * Background color Style
     */
    private var mBackgroundColorColoStyle: RREBackgroundColor? = null


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
        mBoldStyle = mBoldImageView?.let { mContext?.let { it1 -> RREBold(it1, it) } }
        mItalicStyle = mItalicImageView?.let { mContext?.let { it1 -> RREItalic(it1, it) } }
        mStrikethroughStyle = mStrikethroughImageView?.let { mContext?.let { it1 -> RREStrikethrough(it1, it) } }
        mUnderlineStyle = mUnderlineImageView?.let { mContext?.let { it1 -> RREUnderline(it1, it) } }
        mFontColorStyle = mFontColorImageView?.let { mContext?.let { it1 -> RREFontColor(it1, it) } }
        mBackgroundColorColoStyle = mBackgroundImageView?.let { mContext?.let { it1 -> RREBackgroundColor(it1, it,Color.YELLOW) } }
        mTextLinkStyle = mTextLinkImageView?.let { mContext?.let { it1 -> RRELink(it1, it) } }

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

        mColorPalette =this.findViewById(R.id.rteColorPalette)
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


    fun getStylesList(): ArrayList<IRREStyle> {
        return mStylesList
    }

    fun setColorPaletteColor(color: Int) {
        mColorPalette?.setColor(color)
    }

    fun toggleColorPalette(colorPickerListener: ColorPickerListener?, mContext: Context) {
        mColorPalette =(mContext as EditNote).findViewById(R.id.rteColorPalette)
        val visibility = mColorPalette?.visibility
        mColorPalette?.setColorPickerListener(colorPickerListener)
        if (View.VISIBLE == visibility) {
            mColorPalette?.visibility = View.GONE
        } else {
            mColorPalette?.visibility = View.VISIBLE
        }
    }
}