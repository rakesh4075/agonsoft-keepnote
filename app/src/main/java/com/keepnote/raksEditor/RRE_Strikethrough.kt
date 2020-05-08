package com.keepnote.raksEditor

import android.content.Context
import android.text.style.StrikethroughSpan
import android.util.Log
import android.widget.ImageView

class RRE_Strikethrough: RRE_ABS_Style<StrikethroughSpan> {

    private var mStrikethroughImageView: ImageView? = null

    private var mStrikethroughChecked = false

    private var mEditText: RREEditText? = null

    constructor(context: Context):super(context)

        /**
     * @param boldImage
     */
    constructor(context: Context,StrikethroughImage: ImageView):super(context){
            mStrikethroughImageView = StrikethroughImage
        setListenerForImageView(mStrikethroughImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText
        Log.d("@@@@","${mEditText?.text}")
    }

    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
           mStrikethroughChecked = !mStrikethroughChecked
           RRE_Helper.updateCheckStatus(this, mStrikethroughChecked)
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
