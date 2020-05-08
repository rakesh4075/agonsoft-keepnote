package com.keepnote.raksEditor

import android.content.Intent


interface IRRE_Toolbar {

    fun addToolbarItem(toolbarItem: IRRE_ToolItem?)

    fun getToolItems(): List<IRRE_ToolItem?>?

    fun setEditText(editText: RREEditText?)

    fun getEditText(): RREEditText?

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}