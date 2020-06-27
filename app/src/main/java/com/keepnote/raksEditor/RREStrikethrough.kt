package com.keepnote.raksEditor

import android.content.Context
import android.text.style.StrikethroughSpan
import android.widget.ImageView

class RREStrikethrough(context: Context, StrikethroughImage: ImageView) :
    RREABSStyle<StrikethroughSpan>(context) {

    private var mStrikethroughImageView: ImageView? = StrikethroughImage

    private var mStrikethroughChecked = false

    private var mEditText: RREEditText? = null


    init {
        setListenerForImageView(mStrikethroughImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText

    }

    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
           mStrikethroughChecked = !mStrikethroughChecked
           RREHelper.updateCheckStatus(this, mStrikethroughChecked)
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
        return mStrikethroughImageView
    }

    override fun setChecked(isChecked: Boolean) {
        mStrikethroughChecked = isChecked
    }

    override fun getIsChecked(): Boolean {
      return  mStrikethroughChecked
    }


    override fun newSpan(): StrikethroughSpan {
        return StrikethroughSpan()
    }

}
