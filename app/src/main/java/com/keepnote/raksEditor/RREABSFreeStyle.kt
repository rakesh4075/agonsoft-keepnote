package com.keepnote.raksEditor

import android.content.Context
import android.widget.EditText
import android.widget.ImageView

abstract class RREABSFreeStyle(
    toolbar: RRE_Toolbar,
    mEditText: RREEditText?
) :IRREStyle {

    private var rreToolbar:RRE_Toolbar? = toolbar

    private var mEditTextt: EditText? = null

    private var mContext: Context? = null

    init {
        if (rreToolbar!= null){



            mContext = toolbar.context

            mEditTextt = mEditText

        }
    }

    override fun getEditText(): EditText? {
        return mEditTextt
    }



    override fun getIsChecked(): Boolean {
        return false
    }

    override fun setListenerForImageView(imageView: ImageView?) {
        TODO("Not yet implemented")
    }
}