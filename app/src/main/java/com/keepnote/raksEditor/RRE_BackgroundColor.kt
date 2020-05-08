package com.keepnote.raksEditor

import android.content.Context
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.widget.ImageView

class RRE_BackgroundColor: RRE_ABS_Style<BackgroundColorSpan> {

    private var mBackgroundImageView: ImageView? = null

    private var mBackgroundChecked = false

    private var mEditText: RREEditText? = null

    private var mColor = 0

    constructor(context: Context):super(context)

        /**
     * @param boldImage
     */
    constructor(context: Context,backgroundImage: ImageView,backgroundColor:Int):super(context){
            mBackgroundImageView = backgroundImage
            mColor = backgroundColor
        setListenerForImageView(mBackgroundImageView)
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
           mBackgroundChecked = !mBackgroundChecked
           RRE_Helper.updateCheckStatus(this, mBackgroundChecked)
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
