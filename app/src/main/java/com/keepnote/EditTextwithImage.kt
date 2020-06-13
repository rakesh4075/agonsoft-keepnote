package com.keepnote

import android.content.Context
import android.text.Spannable
import androidx.appcompat.widget.AppCompatEditText
import java.util.jar.Attributes

class EditTextwithImage(context: Context): AppCompatEditText(context) {

    constructor(context: Context,attributes: Attributes):this(context)

    val spannableFactory = Spannable.Factory.getInstance()


    override fun setText(text: CharSequence?, type: BufferType?) {
        val s = getTextWithImages(context, text)
        super.setText(text, type)

    }

    private fun getTextWithImages(context: Context?, text: CharSequence?): Spannable {
        val spannable = spannableFactory.newSpannable(text)
        addImage(context,spannable)
        return spannable
    }

    private fun addImage(context: Context?, spannable: Spannable?) {

    }
}