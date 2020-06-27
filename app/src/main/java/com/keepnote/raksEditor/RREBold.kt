package com.keepnote.raksEditor

import android.content.Context
import android.widget.ImageView

class RREBold
/**
 * @param boldImage
 */(context: Context, boldImage: ImageView) : RREABSStyle<RREBoldSpan>(context) {

    private var mBoldImageView: ImageView? = boldImage

    private var mBoldChecked = false

    private var mEditText: RREEditText? = null


    init {
        setListenerForImageView(mBoldImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText

    }

    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
           mBoldChecked = !mBoldChecked
           RREHelper.updateCheckStatus(this, mBoldChecked)
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
        return mBoldImageView
    }

    override fun setChecked(isChecked: Boolean) {
        mBoldChecked = isChecked
    }

    override fun getIsChecked(): Boolean {
      return  mBoldChecked
    }


    override fun newSpan(): RREBoldSpan {
        return RREBoldSpan()
    }

}
