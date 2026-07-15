package com.example.bidifix.util

import android.content.Intent

/** Extracts plain text delivered through share (`ACTION_SEND`) and `ACTION_PROCESS_TEXT`. */
object ShareIntentParser {

    /**
     * Returns the shared/selected text carried by [intent], or `null` when the intent is
     * not a supported text-sharing intent or carries no text.
     */
    fun parse(intent: Intent?): String? {
        if (intent == null) return null
        return when (intent.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.takeIf { it.isNotEmpty() }
                } else {
                    null
                }
            }

            Intent.ACTION_PROCESS_TEXT -> {
                intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
                    ?.toString()
                    ?.takeIf { it.isNotEmpty() }
            }

            else -> null
        }
    }
}
