package com.keepnote.raksEditor

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.keepnote.R


class RRE_ToolItem_Bold(var context: Context) : RRE_ToolItem_Abstract() {

    override fun getStyle(): IRRE_Style? {
        if (mStyle == null) {
            val editText: RREEditText? = getEditText()
            val toolItemUpdater: IRRE_ToolItem_Updater? = getToolItemUpdater()
            mStyle = toolItemUpdater?.let { editText?.let { it1 ->
                RRE_Style_Bold(context,
                    it1, mToolItemView as ImageView,checkUpdater = it)
            } }

        }
        return mStyle
    }

    override fun getView(context: Context?): View? {
        if (null == context) {
            return mToolItemView
        }
        if (mToolItemView == null) {
            val imageView = ImageView(context)
            val size = Util.getPixelByDp(context, 40)
            val params = LinearLayout.LayoutParams(size, size)
            imageView.layoutParams = params
            imageView.setImageResource(R.drawable.bold)
            imageView.bringToFront()
            mToolItemView = imageView
        }

        return mToolItemView
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        TODO("Not yet implemented")
    }

    override fun getToolItemUpdater(): IRRE_ToolItem_Updater? {
        if (mToolItemUpdater==null){
            mToolItemUpdater = RRE_ToolItem_UpdaterDefault(this, Constants.CHECKED_COLOR, Constants.UNCHECKED_COLOR)
            setToolItemUpdater(mToolItemUpdater)
        }
        return mToolItemUpdater
    }
}