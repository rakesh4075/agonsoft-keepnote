package com.keepnote.raksEditor

import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.ImageView

abstract class RRE_ABS_FreeStyle(
    toolbar: RRE_Toolbar,
    mEditText: RREEditText?
) :IRRE_Style {

    private var rreToolbar:RRE_Toolbar? = toolbar

    protected var mEditTextt: EditText? = null

    protected var mContext: Context? = null

    init {
        if (rreToolbar!= null){



            mContext = toolbar.context

            mEditTextt = mEditText

            if (mEditTextt!=null){

                Log.d("@@@@","toolbar not null")
            }

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