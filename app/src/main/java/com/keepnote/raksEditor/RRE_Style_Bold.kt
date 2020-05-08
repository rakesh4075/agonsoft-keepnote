package com.keepnote.raksEditor

import android.content.Context
import android.widget.EditText
import android.widget.ImageView

class RRE_Style_Bold : RRE_ABS_Style<RREBoldSpan> {
    private var mBoldImageView: ImageView? = null

    private var mBoldChecked = false

    private var mEditText: RREEditText? = null

    private var mCheckUpdater: IRRE_ToolItem_Updater? = null


    constructor(context: Context):super(context)

    constructor(context: Context, editText: RREEditText, boldImage:ImageView, checkUpdater: IRRE_ToolItem_Updater):this(context = context){
        mEditText = editText
        mBoldImageView = boldImage
        mCheckUpdater = checkUpdater
        setListenerForImageView(mBoldImageView)
    }

    override fun newSpan(): RREBoldSpan {
        return RREBoldSpan()
    }

    override fun setListenerForImageView(imageView: ImageView?) {
        imageView!!.setOnClickListener {
            mBoldChecked = !mBoldChecked
            if (mCheckUpdater != null) {
                mCheckUpdater?.onCheckStatusUpdate(mBoldChecked)
            }
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

    override fun getEditText(): EditText? {
        return mEditText
    }

    override fun getIsChecked(): Boolean {
        return mBoldChecked
    }

    override fun setChecked(isChecked: Boolean) {
        mBoldChecked = isChecked
    }
}