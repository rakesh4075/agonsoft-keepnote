package com.keepnote.raksEditor

import android.content.Context
import android.widget.ImageView

class RRE_Underline: RRE_ABS_Style<RREUnderlineSpan> {

    private var mUnderlineImageView: ImageView? = null

    private var mUnderlineChecked = false

    private var mEditText: RREEditText? = null

    constructor(context: Context):super(context)

        /**
     * @param boldImage
     */
    constructor(context: Context,UnderlineImage: ImageView):super(context){
            mUnderlineImageView = UnderlineImage
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
           RRE_Helper.updateCheckStatus(this, mUnderlineChecked)
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
