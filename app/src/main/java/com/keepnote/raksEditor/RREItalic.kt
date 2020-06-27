package com.keepnote.raksEditor

import android.content.Context
import android.widget.ImageView

class RREItalic(context: Context, italicImage: ImageView) : RREABSStyle<RREItalicSpan>(context) {

    private var mItalicImageView: ImageView? = italicImage

    private var mItalicChecked = false

    private var mEditText: RREEditText? = null


    init {
        setListenerForImageView(mItalicImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText

    }

    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
           mItalicChecked = !mItalicChecked
           RREHelper.updateCheckStatus(this,mItalicChecked)
           if (null != mEditText) {
               applyStyle(
                   mEditText!!.editableText,
                   mEditText!!.selectionStart,
                   mEditText!!.selectionEnd
               )
           }
       }
    }

    override fun getImageView(): ImageView? {
        return mItalicImageView
    }

    override fun setChecked(isChecked: Boolean) {
        mItalicChecked = isChecked
    }

    override fun getIsChecked(): Boolean {
      return  mItalicChecked
    }


    override fun newSpan(): RREItalicSpan {
        return RREItalicSpan()
    }

}
