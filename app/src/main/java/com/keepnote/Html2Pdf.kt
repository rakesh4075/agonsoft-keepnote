package com.keepnote

import android.content.Context
import android.print.PdfConverter
import android.print.PdfConverter.Companion.OnComplete
import java.io.File

class Html2Pdf private constructor(
    private val context: Context,
    private val html: String,
    private val file: File) {

    fun convertToPdf(onCompleteConversion: OnCompleteConversion) {
        PdfConverter.instance.convert(context, html, file, object : OnComplete {
            override fun onWriteComplete() {
                onCompleteConversion.onSuccess("Exported successfully")
            }

            override fun onWriteFailed() {
                onCompleteConversion.onFailed("Export failed")
            }

        })
    }

    fun convertToPdf() {
        PdfConverter.instance.convert(context, html, file, null)
    }

    interface OnCompleteConversion {

        fun onSuccess(msg:String)

        fun onFailed(msg:String)

    }

    companion object {

        class Builder {

            private lateinit var context: Context
            private lateinit var html: String
            private lateinit var file: File

            fun context(context: Context): Builder {
                this.context = context
                return this
            }

            fun html(html: String): Builder {
                this.html = html
                return this
            }

            fun file(file: File): Builder {
                this.file = file
                return this
            }

            fun build(): Html2Pdf {
                return Html2Pdf(context, html, file)
            }

        }

    }

}