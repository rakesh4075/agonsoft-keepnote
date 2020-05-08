package com.keepnote.raksEditor

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.keepnote.EditNote
import com.keepnote.R


class RRE_Link
/**
 * @param boldImage
 */(context: Context, boldImage: ImageView):IRRE_Style {

    private lateinit var urlText: String
    private val HTTP = "http://"
    private val HTTPS = "https://"

    private var mTextLinkImageView: ImageView? = boldImage

    private var mBoldChecked = false

    private var mEditText: RREEditText? = null

    private var mcontext:Context?=null


    init {
        mcontext = context
        setListenerForImageViews(mTextLinkImageView)
    }

    /**
     * @param editText
     */
    fun setEditText(editText: RREEditText?) {
        mEditText = editText
        Log.d("@@@@","${mEditText?.text}")
    }

    override fun setListenerForImageView(imageView: ImageView?) {
        TODO("Not yet implemented")
    }

    fun setListenerForImageViews(imageView: ImageView?) {
       imageView?.setOnClickListener {
           mBoldChecked = !mBoldChecked
           RRE_Helper.updateCheckStatus(this, mBoldChecked)
           if (null != mEditText) {
               openLinkDialog()
           }
       }
    }

    override fun applyStyle(editable: Editable?, start: Int, end: Int) {

    }

    override fun getImageView(): ImageView? {
        return null
    }

    override fun setChecked(isChecked: Boolean) {

    }

    override fun getIsChecked(): Boolean {
        return true
    }

    override fun getEditText(): EditText? {
        return null
    }

    private fun openLinkDialog() {
        val activity = mcontext as EditNote
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.are_link_title)

        val layoutInflater = activity.layoutInflater
        val areInsertLinkView: View = layoutInflater.inflate(R.layout.are_link_insert, null)

        builder.setView(areInsertLinkView) // Add the buttons
            .setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, id ->
                    val editText =
                        areInsertLinkView.findViewById<View>(R.id.are_insert_link_edit) as EditText
                    val url = editText.text.toString()
                    if (TextUtils.isEmpty(url)) {
                        dialog.dismiss()
                        return@OnClickListener
                    }
                    insertLink(url)
                })
        builder.setNegativeButton("Cancel") { dialog, id -> dialog.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun insertLink(url: String) {
        if (TextUtils.isEmpty(url)) {
            return
        }

        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
            urlText = HTTP + url
        }

        if (null != mEditText) {
            val editable = mEditText!!.editableText
            val start = mEditText!!.selectionStart
            var end = mEditText!!.selectionEnd
            if (start == end) {
                editable.insert(start, url)
                end = start+url.length
            }
            editable.setSpan(RREUrlSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }



}
