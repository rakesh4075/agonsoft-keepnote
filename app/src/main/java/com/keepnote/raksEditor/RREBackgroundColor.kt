package com.keepnote.raksEditor

import android.content.Context
import android.text.style.BackgroundColorSpan
import android.widget.ImageView

class RREBackgroundColor(context: Context, backgroundImage: ImageView, backgroundColor: Int) :
    RREABSStyle<BackgroundColorSpan>(context) {

    private var mBackgroundImageView: ImageView? = backgroundImage

    private var mBackgroundChecked = false

    private var mEditText: RREEditText? = null

    private var mColor = backgroundColor


    init {
        setListenerForImageView(mBackgroundImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText

    }

    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
           mBackgroundChecked = !mBackgroundChecked
           RREHelper.updateCheckStatus(this, mBackgroundChecked)
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
        return mBackgroundImageView
    }

    override fun setChecked(isChecked: Boolean) {
        mBackgroundChecked = isChecked
    }

    override fun getIsChecked(): Boolean {
      return  mBackgroundChecked
    }


    override fun newSpan(): BackgroundColorSpan {
        return BackgroundColorSpan(mColor)
    }

}
