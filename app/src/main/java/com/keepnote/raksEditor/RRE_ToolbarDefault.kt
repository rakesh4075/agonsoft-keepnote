package com.keepnote.raksEditor

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.Gravity
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

class RRE_ToolbarDefault:HorizontalScrollView, IRRE_Toolbar {

    private lateinit var mContainer: LinearLayout
    private var mToolItems: ArrayList<IRRE_ToolItem> = ArrayList()

    constructor(context: Context):super(context,null)

    constructor(context: Context,attrs:AttributeSet):super(context,attrs,0)

    constructor(context: Context,attr: AttributeSet,defStyle:Int):super(context,attr)

    init {
        initSelf()
    }



    override fun addToolbarItem(toolbarItem: IRRE_ToolItem?) {
        toolbarItem?.setToolbar(this)
        toolbarItem?.let { mToolItems.add(it) }
        val view = toolbarItem?.getView(context)
        if (view != null) {
            mContainer.addView(view)
        }

    }

    override fun getToolItems(): List<IRRE_ToolItem?>? {
        TODO("Not yet implemented")
    }

    override fun setEditText(editText: RREEditText?) {
        TODO("Not yet implemented")
    }

    override fun getEditText(): RREEditText? {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        TODO("Not yet implemented")
    }


    private fun initSelf() {
        mContainer = LinearLayout(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mContainer.gravity = Gravity.CENTER_VERTICAL
        mContainer.layoutParams = params
        this.addView(mContainer)
    }
}