package com.keepnote.raksEditor

import android.text.Editable
import android.widget.EditText
import android.widget.ImageView

interface IRREStyle {
    /**
     * For styles like Bold / Italic / Underline, by clicking the ImageView,
     * we should change the UI, so user can notice that this style takes
     * effect now.
     *
     * @param imageView
     */
    fun setListenerForImageView(imageView: ImageView?)


    /**
     * Apply the style to the change start at start end at end.
     *
     * @param editable
     * @param start
     * @param end
     */
    fun applyStyle(editable: Editable?, start: Int, end: Int)


    /**
     * Returns the [ImageView] of this style.
     *
     * @return
     */
    fun getImageView(): ImageView?

    /**
     * Sets if this style is checked.
     *
     * @param isChecked
     */
    fun setChecked(isChecked: Boolean)

    /**
     * Returns if current style is checked.
     *
     * @return
     */
    fun getIsChecked(): Boolean

    /**
     * Gets the EditText being operated.
     *
     * @return
     */
    fun getEditText(): EditText?
}