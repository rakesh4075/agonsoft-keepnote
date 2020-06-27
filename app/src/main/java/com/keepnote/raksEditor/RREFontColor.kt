package com.keepnote.raksEditor

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import com.keepnote.R
import com.keepnote.colorpicker.ColorPicker


class RREFontColor
(context: Context, fontColorImage: ImageView) :
    ARE_ABS_Dynamic_Style<AreForegroundColorSpan>(context) {


    private var mFontColorImageView: ImageView? = fontColorImage

    private var mFontColorChecked = false




    private var mColor = -1

    private var mEditText: RREEditText? = null





    init {
        setListenerForImageView(mFontColorImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText
    }

    override fun setListenerForImageView(imageView: ImageView?) {
       imageView?.setOnClickListener {
         //  if (mContext!=null)
           //RRE_Toolbar(mContext!!).toggleColorPalette(mColorPickerListener)
           if (mContext!=null){
               val coloPicker = ColorPicker(mContext!! as Activity)
               coloPicker.setOnFastChooseColorListener(object :ColorPicker.OnFastChooseColorListener{
                   override fun setOnFastChooseColorListener(position: Int, color: Int) {
                       mColor = color
                   }

                   override fun onCancel() {

                   }
               })

                   .setColumns(5)
                   .setColors(R.array.default_colors)
                   .show()
           }


           //if (mContext!=null)  mColor = ContextCompat.getColor(mContext!!, R.color.colorPrimary)

           if (null != mEditText) {
               val editable = mEditText!!.editableText
               val start = mEditText!!.selectionStart
               val end = mEditText!!.selectionEnd
               if (end > start) {
                   applyNewStyle(editable, start, end, mColor)
               }
           }
       }
    }

    override fun getImageView(): ImageView? {
        return mFontColorImageView
    }

    override fun setChecked(isChecked: Boolean) {
        mFontColorChecked = isChecked
    }

    override fun getIsChecked(): Boolean {
        return mColor != -1
    }

    override fun changeSpanInsideStyle(editable: Editable?, start: Int, end: Int, existingSpan: AreForegroundColorSpan?) {
        val currentColor: Int? = existingSpan?.foregroundColor
        if (currentColor != mColor) {
            applyNewStyle(editable, start, end, mColor)
            logAllFontColorSpans(editable)
        }
    }

    private fun logAllFontColorSpans(editable: Editable?) {
        if (editable!=null){
            val listItemSpans = editable.getSpans(0, editable.length, ForegroundColorSpan::class.java)
            for (span in listItemSpans) {
                val ss = editable.getSpanStart(span)
                val se = editable.getSpanEnd(span)
            }
        }

    }

    override fun featureChangedHook(lastSpanColor: Int) {
        mColor = lastSpanColor
        if (mContext!=null)
        RRE_Toolbar(mContext!!).getInstance()?.setColorPaletteColor(mColor)
    }

    override fun newSpan(feature: Int): AreForegroundColorSpan {
        return AreForegroundColorSpan(mColor)
    }

    override fun newSpan(): AreForegroundColorSpan {
        return AreForegroundColorSpan(mColor)
    }
}
