package com.keepnote.raksEditor

class RRE_ToolItem_UpdaterDefault(var mToolItem: IRRE_ToolItem, var mCheckedColor:Int, var mUncheckedColor:Int):
    IRRE_ToolItem_Updater {

    override fun onCheckStatusUpdate(checked: Boolean) {
        val rreStyle = mToolItem.getStyle()
        rreStyle?.setChecked(checked)
        val view = mToolItem.getView(null)
        val color: Int = if (checked) mCheckedColor else mUncheckedColor
        view?.setBackgroundColor(color)
    }
}