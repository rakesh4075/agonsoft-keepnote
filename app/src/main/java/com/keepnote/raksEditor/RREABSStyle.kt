package com.keepnote.raksEditor

import android.content.Context
import android.text.Editable
import android.text.Spanned
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import java.lang.reflect.ParameterizedType

abstract class RREABSStyle<E>(context: Context) : IRREStyle {
    protected var mContext: Context? = null

    var clazzE: Class<E>? = null
    init {
        mContext = context
        if (mContext==null){
            mContext = RRE_Toolbar(context).getInstance()?.context
        }
        clazzE =
            (this.javaClass.genericSuperclass as ParameterizedType?)!!.actualTypeArguments[0] as Class<E>
    }

    override fun setListenerForImageView(imageView: ImageView?) {

    }

    override fun applyStyle(editable: Editable?, start: Int, end: Int) {
        if (getIsChecked()){
            if (end > start) {
                val spans = editable?.getSpans(start, end, clazzE)
                var existingESpan: E? = null
                if (spans!=null)
                if (spans.isNotEmpty()) {
                    existingESpan = spans[0]
                }
                if (existingESpan == null) {
                    checkAndMergeSpan(editable!!, start, end, clazzE!!)
                } else {
                    val existingESpanStart = editable!!.getSpanStart(existingESpan)
                    val existingESpanEnd = editable.getSpanEnd(existingESpan)
                    if (existingESpanStart <= start && existingESpanEnd >= end) {
                        // The selection is just within an existing E span
                        // Do nothing for this case
                        changeSpanInsideStyle(editable, start, end, existingESpan)
                    } else {
                        checkAndMergeSpan(editable, start, end, clazzE!!)
                    }
                }
            }else{
                //
                // User deletes

                //
                // User deletes
                val spans = editable!!.getSpans(start, end, clazzE)
                if (spans.isNotEmpty()) {
                    var span = spans[0]
                    var lastSpanStart = editable.getSpanStart(span)
                    for (e in spans) {
                        val lastSpanStartTmp = editable.getSpanStart(e)
                        if (lastSpanStartTmp > lastSpanStart) {
                            lastSpanStart = lastSpanStartTmp
                            span = e
                        }
                    }
                    val eStart = editable.getSpanStart(span)
                    val eEnd = editable.getSpanEnd(span)

                    if (eStart >= eEnd) {
                        editable.removeSpan(span)
                        extendPreviousSpan(editable, eStart)
                        setChecked(false)
                        RREHelper.updateCheckStatus(this, false)
                    }
                }
            }
        } else {
            //
            // User un-checks the style
            if (end > start) {
                //
                // User inputs or user selects a range
                val spans = editable!!.getSpans(start, end, clazzE)
                if (spans.isNotEmpty()) {
                    val span = spans[0]
                    if (null != span) {
                        //
                        // User stops the style, and wants to show
                        // un-UNDERLINE characters
                        val ess = editable.getSpanStart(span) // ess == existing span start
                        val ese = editable.getSpanEnd(span) // ese = existing span end
                        if (start >= ese) {
                            // User inputs to the end of the existing e span
                            // End existing e span
                            editable.removeSpan(span)
                            editable.setSpan(span, ess, start - 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        } else if (start == ess && end == ese) {
                            // Case 1 desc:
                            // *BBBBBB*
                            // All selected, and un-check e
                            editable.removeSpan(span)
                        } else if (start > ess && end < ese) {
                            // Case 2 desc:
                            // BB*BB*BB
                            // *BB* is selected, and un-check e
                            editable.removeSpan(span)
                            val spanLeft = newSpan()
                            editable.setSpan(spanLeft, ess, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                            val spanRight = newSpan()
                            editable.setSpan(spanRight, end, ese, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        } else if (start == ess && end < ese) {
                            // Case 3 desc:
                            // *BBBB*BB
                            // *BBBB* is selected, and un-check e
                            editable.removeSpan(span)
                            val newSpan = newSpan()
                            editable.setSpan(newSpan, end, ese, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        } else if (start > ess && end == ese) {
                            // Case 4 desc:
                            // BB*BBBB*
                            // *BBBB* is selected, and un-check e
                            editable.removeSpan(span)
                            val newSpan = newSpan()
                            editable.setSpan(newSpan, ess, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        }
                    }
                }
            } else if (end == start) {
                //
                // User changes focus position
                // Do nothing for this case
            } else {
                //
                // User deletes
                val spans = editable!!.getSpans(start, end, clazzE)
                if (spans.isNotEmpty()) {
                    val span = spans[0]
                    if (null != span) {
                        val eStart = editable.getSpanStart(span)
                        val eEnd = editable.getSpanEnd(span)
                        if (eStart >= eEnd) {
                            //
                            // Invalid case, this will never happen.
                        } else {
                            //
                            // Do nothing, the default behavior is to extend
                            // the span's area.
                            // The proceeding characters should be also
                            // UNDERLINE
                            setChecked(true)
                            RREHelper.updateCheckStatus(this, true)
                        }
                    }
                }
            }
        }
    }
    protected open fun extendPreviousSpan(editable: Editable?, pos: Int) {
        // Do nothing by default
    }

    protected open fun changeSpanInsideStyle(editable: Editable?, start: Int, end: Int, e: E) {
        // Do nothing by default
        Log.e("ARE", "in side a span!!")
    }
    private  fun removeAllSpans(editable: Editable, start: Int, end: Int, clazzE: Class<E>) {
        val allSpans = editable.getSpans(start, end, clazzE)
        for (span in allSpans) {
            editable.removeSpan(span)
        }
    }


    private  fun checkAndMergeSpan(editable: Editable, start: Int, end: Int, clazzE: Class<E>) {
        var leftSpan: E? = null
        val leftSpans = editable.getSpans(start, start, clazzE)
        if (leftSpans.isNotEmpty()) {
            leftSpan = leftSpans[0]
        }
        var rightSpan: E? = null
        val rightSpans = editable.getSpans(end, end, clazzE)
        if (rightSpans.isNotEmpty()) {
            rightSpan = rightSpans[0]
        }
        val leftSpanStart = editable.getSpanStart(leftSpan)
        val rightSpanEnd = editable.getSpanEnd(rightSpan)
        removeAllSpans(editable, start, end, clazzE)
        if (leftSpan != null && rightSpan != null) {
            val eSpan = newSpan()
            editable.setSpan(eSpan, leftSpanStart, rightSpanEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        } else if (leftSpan != null && rightSpan == null) {
            val eSpan = newSpan()
            editable.setSpan(eSpan, leftSpanStart, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        } else if (leftSpan == null && rightSpan != null) {
            val eSpan = newSpan()
            editable.setSpan(eSpan, start, rightSpanEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        } else {
            val eSpan = newSpan()
            editable.setSpan(eSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }


    override fun getImageView(): ImageView? {
       return null
    }

    override fun setChecked(isChecked: Boolean) {

    }


    override fun getEditText(): EditText? {
        return mContext?.let {  RRE_Toolbar(context = it).getEditText() }
    }

    abstract fun newSpan(): E
}