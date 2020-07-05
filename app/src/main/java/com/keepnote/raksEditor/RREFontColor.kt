package com.keepnote.raksEditor

import android.content.Context
import android.text.Editable
import android.widget.ImageView
import com.keepnote.utils.ColorPickerListener


class RREFontColor
(context: Context, fontColorImage: ImageView) :
    ARE_ABS_Dynamic_Style<AreForegroundColorSpan>(context) {


    private var mFontColorImageView: ImageView? = fontColorImage

    private var mFontColorChecked = false




    private var mColor = -1

    private var mEditText: RREEditText? = null





    init {
        setListenerForImageView(mFontColorImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText
    }
    private val mColorPickerListener: ColorPickerListener =
        ColorPickerListener { color ->
            mColor = color
            mFontColorImageView?.setBackgroundColor(mColor)

            if (null != mEditText) {
                val editable = mEditText!!.editableText
                val start = mEditText!!.selectionStart
                val end = mEditText!!.selectionEnd
                if (end > start) {
                    applyNewStyle(editable, start, end, mColor)
                }
            }
        }
    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
           if (mContext!=null)
           RRE_Toolbar(mContext!!).toggleColorPalette(mColorPickerListener, mContext!!)
       }
    }

    override fun getImageView(): ImageView? {
        return mFontColorImageView
    }

    override fun setChecked(isChecked: Boolean) {
        mFontColorChecked = isChecked
    }

    override fun getIsChecked(): Boolean {
        return mColor != -1
    }

    override fun changeSpanInsideStyle(editable: Editable?, start: Int, end: Int, existingSpan: AreForegroundColorSpan?) {
        val currentColor: Int? = existingSpan?.foregroundColor
        if (currentColor != mColor) {
            applyNewStyle(editable, start, end, mColor)
            //logAllFontColorSpans(editable)
        }
    }



    override fun featureChangedHook(lastSpanColor: Int) {
        mColor = lastSpanColor
        if (mContext!=null)
        RRE_Toolbar(mContext!!).getInstance()?.setColorPaletteColor(mColor)
    }

    override fun newSpan(feature: Int): AreForegroundColorSpan {
        return AreForegroundColorSpan(mColor)
    }

    override fun newSpan(): AreForegroundColorSpan {
        return AreForegroundColorSpan(mColor)
    }
}
