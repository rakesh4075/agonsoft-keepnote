package com.keepnote.raksEditor

import android.content.Context
import android.content.Intent
import android.view.View


interface IRREToolItem {
    /**
     * Each tool item is a style, and a style combines with specific span.
     * @return
     */
    fun getStyle(): IRREStyle?

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


    /**
     * Returns the toolbar of this tool item.
     * @return
     */
    fun getToolbar(): IRREToolbar?

    /**
     * Sets the toolbar for this tool item.
     */
    fun setToolbar(toolbar: IRREToolbar?)


    /**
     * Gets the tool item updater instance, will be called when style being checked and unchecked.
     * @return
     */
    fun getToolItemUpdater(): IRREToolItemUpdater?


    /**
     * Sets the tool item updater.
     * @param toolItemUpdater
     */
    fun setToolItemUpdater(toolItemUpdater: IRREToolItemUpdater?)

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}