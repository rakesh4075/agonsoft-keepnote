package com.keepnote.raksEditor

import android.content.Context
import android.util.Log
import android.widget.ImageView

class RRE_Italic: RRE_ABS_Style<RREItalicSpan> {

    private var mItalicImageView: ImageView? = null

    private var mItalicChecked = false

    private var mEditText: RREEditText? = null

    constructor(context: Context):super(context)

        /**
     * @param boldImage
     */
    constructor(context: Context,italicImage: ImageView):super(context){
            mItalicImageView = italicImage
        setListenerForImageView(mItalicImageView)
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
           mItalicChecked = !mItalicChecked
           RRE_Helper.updateCheckStatus(this,mItalicChecked)
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
