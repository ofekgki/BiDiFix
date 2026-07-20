package com.example.bidifix.util

import android.content.Intent

/** Extracts plain text delivered through share (`ACTION_SEND`) and `ACTION_PROCESS_TEXT`. */
object ShareIntentParser {

    /**
     * Returns the shared/selected text carried by [intent], or `null` when the intent is
     * not a supported text-sharing intent or carries no text.
     *
     * `EXTRA_TEXT` and `EXTRA_PROCESS_TEXT` are read as `CharSequence` (via
     * `getCharSequenceExtra`), not `String`, so styled text — e.g. shared from Chrome — is
     * not dropped the way `getStringExtra` would silently drop a `Spanned` value.
     */
    fun parse(intent: Intent?): String? {
        if (intent == null) return null
        return resolve(
            action = intent.action,
            type = intent.type,
            extraText = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString(),
            processText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString(),
        )
    }

    /**
     * Pure selection logic, separated so it can be unit-tested without an Android [Intent].
     * Accepts `text/plain` and parameterised variants such as `text/plain;charset=utf-8`.
     */
    internal fun resolve(
        action: String?,
        type: String?,
        extraText: String?,
        processText: String?,
    ): String? = when (action) {
        Intent.ACTION_SEND ->
            if (type?.startsWith("text/") == true) extraText?.takeIf { it.isNotEmpty() } else null

        Intent.ACTION_PROCESS_TEXT -> processText?.takeIf { it.isNotEmpty() }

        else -> null
    }
}
