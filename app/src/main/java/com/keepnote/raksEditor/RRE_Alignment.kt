package com.keepnote.raksEditor

import android.text.Editable
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.util.Log
import android.widget.ImageView

class RRE_Alignment:RRE_ABS_FreeStyle {

    private var mAlignmentImageView: ImageView? = null

    private var mAlignment: Layout.Alignment? = null



    constructor(
        imageView: ImageView?,
        alignment: Layout.Alignment,
        toolbar: RRE_Toolbar,
        mEditText: RREEditText?
    ) : super(toolbar,mEditText) {
        mAlignmentImageView = imageView
        mAlignment = alignment
        if (mAlignmentImageView!=null){
            Log.d("@@@@2","null")
            setListenerForImageView(mAlignmentImageView)
        }

    }



    override fun setListenerForImageView(imageView: ImageView?) {
        if (getEditText()!=null){
            val editText = getEditText()
            val currentLine = Util.getCurrentCursorLine(editText!!)
            val start = Util.getThisLineStart(editText, currentLine)
            var end = Util.getThisLineEnd(editText, currentLine)

            val editable = editText.editableText

            val alignmentSpans = editable.getSpans(start, end, AlignmentSpan.Standard::class.java)
            if (null != alignmentSpans) {
                for (span in alignmentSpans) {
                    editable.removeSpan(span)
                }
            }

            val alignCenterSpan: AlignmentSpan = AlignmentSpan.Standard(mAlignment!!)
            if (start == end) {
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                end = Util.getThisLineEnd(editText, currentLine)
            }
//            editable.setSpan(alignCenterSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

    }


    override fun applyStyle(editable: Editable?, start: Int, end: Int) {
        val alignmentSpans = editable!!.getSpans(
            start, end,
            AlignmentSpan::class.java
        )
        if (null == alignmentSpans || alignmentSpans.size == 0) {
            return
        }

        val alignment = alignmentSpans[0].alignment
        if (mAlignment != alignment) {
            return
        }

        if (end > start) {
            //
            // User inputs
            //
            // To handle the \n case
            val c = editable[end - 1]
            if (c == Constants.CHAR_NEW_LINE) {
                val alignmentSpansSize = alignmentSpans.size
                val previousAlignmentSpanIndex = alignmentSpansSize - 1
                if (previousAlignmentSpanIndex > -1) {
                    val previousAlignmentSpan =
                        alignmentSpans[previousAlignmentSpanIndex]
                    val lastAlignmentSpanStartPos =
                        editable.getSpanStart(previousAlignmentSpan)
                    if (end > lastAlignmentSpanStartPos) {
                        editable.removeSpan(previousAlignmentSpan)
                        editable.setSpan(
                            previousAlignmentSpan,
                            lastAlignmentSpanStartPos,
                            end - 1,
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                    markLineAsAlignmentSpan(mAlignment!!)
                }
            } // #End of user types \n
        } else {
            //
            // User deletes
            val spanStart = editable.getSpanStart(alignmentSpans[0])
            val spanEnd = editable.getSpanEnd(alignmentSpans[0])
            if (spanStart >= spanEnd) {
                //
                // User deletes the last char of the span
                // So we think he wants to remove the span
                editable.removeSpan(alignmentSpans[0])

                //
                // To delete the previous span's \n
                // So the focus will go to the end of previous span
                if (spanStart > 0) {
                    editable.delete(spanStart - 1, spanEnd)
                }
            }
        }
    }

    override fun getImageView(): ImageView? {
        return null
    }

    override fun setChecked(isChecked: Boolean) {

    }


    private fun markLineAsAlignmentSpan(alignment: Layout.Alignment) {
        val editText = getEditText()
        val currentLine = Util.getCurrentCursorLine(editText!!)
        var start = Util.getThisLineStart(editText, currentLine)
        var end = Util.getThisLineEnd(editText, currentLine)
        val editable = editText.text
        editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
        start = Util.getThisLineStart(editText, currentLine)
        end = Util.getThisLineEnd(editText, currentLine)
        if (end < 1) {
            return
        }
        if (editable[end - 1] == Constants.CHAR_NEW_LINE) {
            end--
        }
        val alignmentSpan: AlignmentSpan = AlignmentSpan.Standard(alignment)
        editable.setSpan(alignmentSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }

}
