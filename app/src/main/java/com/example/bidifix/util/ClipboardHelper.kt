package com.example.bidifix.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/** Thin wrapper around the Android clipboard that preserves invisible Unicode controls. */
object ClipboardHelper {

    const val CLIP_LABEL = "Transformed Bidi Text"

    /**
     * Copies [text] verbatim to the clipboard, including any invisible bidi control
     * characters. Returns `false` (without copying) when [text] is empty.
     */
    fun copy(context: Context, text: String, label: String = CLIP_LABEL): Boolean {
        if (text.isEmpty()) return false
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText(label, text))
        return true
    }
}
