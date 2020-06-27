package com.keepnote.raksEditor

import android.widget.ImageView



class RREHelper {
    companion object{
        fun updateCheckStatus(areStyle: IRREStyle, checked: Boolean) {
            areStyle.setChecked(checked)
            val imageView: ImageView? = areStyle.getImageView()
            val color = if (checked) Constants.CHECKED_COLOR else Constants.UNCHECKED_COLOR
            imageView?.setBackgroundColor(color)
        }
    }

}