package com.keepnote.raksEditor

import android.text.style.URLSpan

class RREUrlSpan(url: String):URLSpan(url),RREClickableSpan

interface RREClickableSpan
