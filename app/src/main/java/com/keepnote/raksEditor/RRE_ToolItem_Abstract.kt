package com.keepnote.raksEditor

import android.content.Intent
import android.view.View
import android.widget.EditText


abstract class RRE_ToolItem_Abstract: IRRE_ToolItem {
    protected var mStyle: IRRE_Style? = null

    protected var mToolItemView: View? = null

    protected var mToolItemUpdater: IRRE_ToolItem_Updater? = null

    private var mToolbar: IRRE_Toolbar? = null

    override fun setToolbar(toolbar: IRRE_Toolbar?) {
        mToolbar = toolbar
    }

    override fun getToolbar(): IRRE_Toolbar? {
       if (mToolbar!=null)
           return mToolbar
        else throw Exception("Toolbar not iniziallized")
    }


    override fun getToolItemUpdater(): IRRE_ToolItem_Updater? {
        if (mToolbar!=null)
            return mToolItemUpdater
        else throw Exception("mToolItemUpdater not iniziallized")
    }

    override fun setToolItemUpdater(toolItemUpdater: IRRE_ToolItem_Updater?) {
        mToolItemUpdater = toolItemUpdater
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       return
    }

     fun getEditText(): RREEditText? {
        return mToolbar?.getEditText()
    }

    protected open fun <T> printSpans(clazz: Class<T>) {
        val editText: EditText? = getEditText()
        val editable = editText?.editableText
        if (editable!=null){
            val spans = editable.getSpans(0, editable.length, clazz)
            for (span in spans) {
                val start = editable.getSpanStart(span)
                val end = editable.getSpanEnd(span)
                Util.log("Span -- $clazz, start = $start, end == $end")
            }
        }

    }
}