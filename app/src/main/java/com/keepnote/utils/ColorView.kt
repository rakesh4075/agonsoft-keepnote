package com.keepnote.utils

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

class ColorView : LinearLayout {
    companion object{
        val ATTR_VIEW_WIDTH = "ATTR_VIEW_WIDTH"

        val ATTR_VIEW_HEIGHT = "ATTR_VIEW_HEIGHT"

        val ATTR_MARGIN_LEFT = "ATTR_MARGIN_LEFT"

        val ATTR_MARGIN_RIGHT = "ATTR_MARGIN_RIGHT"

        val ATTR_CHECKED_TYPE = "ATTR_CHECKED_TYPE"
    }


    /**
     * If this view width = 80, the the default check view width = 10
     */
    private var DEFAULT_CHECK_VIEW_PERCENT = 8

    private var CHECKMARK_CHECK_VIEW_PERCENT = 2

    private var CHECK_TYPE_DEFAULT = 0

    private var CHECK_TYPE_CHECK_MARK = 1

    private var mContext: Context? = null

    private var mColorViewWidth = 0

    private var mColorViewHeight = 0

    private var mColorViewMarginLeft = 0

    private var mColorViewMarginRight = 0

    private var mColorViewCheckedType = 0

    private var mColor = 0

    private var mChecked = false

    private var mCheckView: View? = null

    constructor(context: Context, color: Int, attributeBundle: Bundle):super(context){
        mContext = context
        mColor = color

        mColorViewWidth = attributeBundle.getInt(ATTR_VIEW_WIDTH, 40)
        mColorViewHeight = attributeBundle.getInt(ATTR_VIEW_HEIGHT, 40)
        mColorViewMarginLeft = attributeBundle.getInt(ATTR_MARGIN_LEFT, 2)
        mColorViewMarginRight = attributeBundle.getInt(ATTR_MARGIN_RIGHT, 2)
        mColorViewCheckedType = attributeBundle.getInt(ATTR_CHECKED_TYPE, 0)
        initView()
    }

    private fun initView() {
        mCheckView = getCheckView(false)
        val layoutParams =
            LayoutParams(mColorViewWidth, mColorViewHeight)
        layoutParams.setMargins(mColorViewMarginLeft, 0, mColorViewMarginRight, 10)
        this.layoutParams = layoutParams
        this.setBackgroundColor(mColor)
        this.gravity = Gravity.CENTER
        this.addView(mCheckView)
    }

    constructor(context: Context,attributeSet: AttributeSet,defstyle:Int):super(context,attributeSet,defstyle){

    }

    fun getCheckView(showTick: Boolean): View? {
        if (mCheckView == null) {
            when (mColorViewCheckedType) {

                CHECK_TYPE_CHECK_MARK -> mCheckView = ColorCheckedViewCheckmark(mContext!!, mColorViewWidth / CHECKMARK_CHECK_VIEW_PERCENT,showTick)

            }
        }else  if (mCheckView!=null){
            when (mColorViewCheckedType) {

                CHECK_TYPE_CHECK_MARK -> {
                    mCheckView = ColorCheckedViewCheckmark(mContext!!, mColorViewWidth / CHECKMARK_CHECK_VIEW_PERCENT,showTick)
                }

            }
        }
        return mCheckView
    }

    fun setColor(color: Int) {
        mColor = color
        initView()
    }

    fun getColor(): Int {
        return mColor
    }

    fun setCheckView(checkedView: View?) {
        mCheckView = checkedView
    }

    fun setChecked(checked: Boolean) {
        mChecked = checked

        initCheckStatus(mChecked)
    }

    private fun initCheckStatus(mChecked: Boolean) {
        if (mCheckView == null) {
            return
        }

        if (this.mChecked) {
            mCheckView =  getCheckView(mChecked)
            mCheckView?.visibility = View.VISIBLE
        } else {
            mCheckView = getCheckView(mChecked)
            mCheckView?.visibility = View.GONE
        }
    }

    fun getChecked(): Boolean {
        return mChecked
    }

    fun show(show: Boolean) {
        if (mCheckView!=null && !show){
            this.visibility = View.GONE
        }
    }
}