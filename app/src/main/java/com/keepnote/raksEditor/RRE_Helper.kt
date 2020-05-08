package com.keepnote.raksEditor

import android.widget.ImageView



class RRE_Helper {
    /**
     * Updates the check status.
     *
     * @param areStyle
     * @param checked
     */
    companion object{
        fun updateCheckStatus(areStyle: IRRE_Style, checked: Boolean) {
            areStyle.setChecked(checked)
            val imageView: ImageView? = areStyle.getImageView()
            val color = if (checked) Constants.CHECKED_COLOR else Constants.UNCHECKED_COLOR
            imageView?.setBackgroundColor(color)
        }
    }

}