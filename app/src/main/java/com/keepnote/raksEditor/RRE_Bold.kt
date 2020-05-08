package com.keepnote.raksEditor

import android.content.Context
import android.util.Log
import android.widget.ImageView

class RRE_Bold: RRE_ABS_Style<RREBoldSpan> {

    private var mBoldImageView: ImageView? = null

    private var mBoldChecked = false

    private var mEditText: RREEditText? = null

    constructor(context: Context):super(context)

        /**
     * @param boldImage
     */
    constructor(context: Context,boldImage: ImageView):super(context){
        mBoldImageView = boldImage
        setListenerForImageView(mBoldImageView)
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
           mBoldChecked = !mBoldChecked
           RRE_Helper.updateCheckStatus(this, mBoldChecked)
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
