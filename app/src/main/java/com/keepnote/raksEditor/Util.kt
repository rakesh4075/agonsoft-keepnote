package com.keepnote.raksEditor

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.text.Selection
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.EditText

class Util {

    companion object{
        fun getScreenWidthAndHeight(context: Context): IntArray? {
            val outSize = Point()
            val windowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            display.getSize(outSize)
            val widthAndHeight = IntArray(2)
            widthAndHeight[0] = outSize.x
            widthAndHeight[1] = outSize.y
            return widthAndHeight
        }

        fun getPixelByDp(context: Context?, dp: Int): Int {
            var pixels = dp
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            pixels = (displayMetrics.density * dp + 0.5).toInt()
            return pixels
        }

        fun log(s: String?) {
            Log.d("RAKS", s)
        }

        fun getCurrentCursorLine(editText: EditText): Int {
            val selectionStart = Selection.getSelectionStart(editText.text)
            val layout = editText.layout ?: return -1
            return if (selectionStart != -1) {
                layout.getLineForOffset(selectionStart)
            } else -1
        }


        fun getThisLineStart(editText: EditText, currentLine: Int): Int {
            var currentLine = currentLine
            val layout = editText.layout
            var start = 0
            if (currentLine > 0) {
                start = layout.getLineStart(currentLine)
                if (start > 0) {
                    val text = editText.text.toString()
                    var lastChar = text[start - 1]
                    while (lastChar != '\n') {
                        if (currentLine > 0) {
                            currentLine--
                            start = layout.getLineStart(currentLine)
                            lastChar = if (start > 1) {
                                start--
                                text[start]
                            } else {
                                break
                            }
                        }
                    }
                }
            }
            return start
        }

        /**
         * Returns the line end position of the current line (which cursor is focusing now).
         *
         * @param editText
         * @return
         */
        fun getThisLineEnd(editText: EditText, currentLine: Int): Int {
            val layout = editText.layout
            return if (-1 != currentLine) {
                layout.getLineEnd(currentLine)
            } else -1
        }
    }

}