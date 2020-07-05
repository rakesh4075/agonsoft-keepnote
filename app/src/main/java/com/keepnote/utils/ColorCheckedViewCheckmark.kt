package com.keepnote.utils

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.keepnote.R

class ColorCheckedViewCheckmark:AppCompatImageView {
    var mContext: Context? = null
    var mSize = 0
    var mshowTick:Boolean = false
    constructor(context: Context,size:Int, showTick:Boolean):super(context){

        mContext = context
        mSize = size
        mshowTick = showTick
        initView()
    }

    private fun initView() {
        val layoutParams = LinearLayout.LayoutParams(mSize, mSize)
        layoutParams.gravity = Gravity.CENTER
        this.layoutParams = layoutParams
        if (mshowTick)
        this.setImageResource(R.drawable.ic_check_mark)

    }

    constructor(context: Context, attributeSet: AttributeSet, defstyle:Int):super(context,attributeSet,defstyle){

    }
}