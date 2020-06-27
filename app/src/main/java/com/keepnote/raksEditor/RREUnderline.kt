package com.keepnote.raksEditor

import android.content.Context
import android.widget.ImageView

class RREUnderline(context: Context, UnderlineImage: ImageView) :
    RREABSStyle<RREUnderlineSpan>(context) {

    private var mUnderlineImageView: ImageView? = UnderlineImage

    private var mUnderlineChecked = false

    private var mEditText: RREEditText? = null


    init {
        setListenerForImageView(mUnderlineImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText
    }

    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
           mUnderlineChecked = !mUnderlineChecked
           RREHelper.updateCheckStatus(this, mUnderlineChecked)
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
        return mUnderlineImageView
    }

    override fun setChecked(isChecked: Boolean) {
        mUnderlineChecked = isChecked
    }

    override fun getIsChecked(): Boolean {
      return  mUnderlineChecked
    }


    override fun newSpan(): RREUnderlineSpan {
        return RREUnderlineSpan()
    }

}
