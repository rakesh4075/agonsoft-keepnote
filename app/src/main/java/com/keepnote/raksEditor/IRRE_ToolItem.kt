package com.keepnote.raksEditor

import android.content.Context
import android.content.Intent
import android.view.View


interface IRRE_ToolItem {
    /**
     * Each tool item is a style, and a style combines with specific span.
     * @return
     */
    fun getStyle(): IRRE_Style?

    /**
     * Each tool item has a view. If context is null, return the generated view.
     */
    fun getView(context: Context?): View?


    /**
     * Selection changed call back. Update tool item checked status
     *
     * @param selStart
     * @param selEnd
     */
    fun onSelectionChanged(selStart: Int, selEnd: Int)

    /**
     * Returns the toolbar of this tool item.
     * @return
     */
    fun getToolbar(): IRRE_Toolbar?

    /**
     * Sets the toolbar for this tool item.
     */
    fun setToolbar(toolbar: IRRE_Toolbar?)


    /**
     * Gets the tool item updater instance, will be called when style being checked and unchecked.
     * @return
     */
    fun getToolItemUpdater(): IRRE_ToolItem_Updater?


    /**
     * Sets the tool item updater.
     * @param toolItemUpdater
     */
    fun setToolItemUpdater(toolItemUpdater: IRRE_ToolItem_Updater?)

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}