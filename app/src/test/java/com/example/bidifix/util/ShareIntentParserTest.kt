package com.example.bidifix.util

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Tests the pure share-intent selection logic (no Android Intent instance needed). */
class ShareIntentParserTest {

    @Test fun send_plainText_isAccepted() {
        assertEquals(
            "hello עולם",
            ShareIntentParser.resolve(Intent.ACTION_SEND, "text/plain", "hello עולם", null),
        )
    }

    @Test fun send_parameterisedTextType_isAccepted() {
        assertEquals(
            "abc",
            ShareIntentParser.resolve(Intent.ACTION_SEND, "text/plain;charset=utf-8", "abc", null),
        )
    }

    @Test fun send_nonTextType_isRejected() {
        assertNull(ShareIntentParser.resolve(Intent.ACTION_SEND, "image/png", "abc", null))
    }

    @Test fun send_emptyText_isNull() {
        assertNull(ShareIntentParser.resolve(Intent.ACTION_SEND, "text/plain", "", null))
    }

    @Test fun send_nullText_isNull() {
        assertNull(ShareIntentParser.resolve(Intent.ACTION_SEND, "text/plain", null, null))
    }

    @Test fun processText_isReadFromProcessExtra() {
        assertEquals(
            "selected טקסט",
            ShareIntentParser.resolve(Intent.ACTION_PROCESS_TEXT, null, null, "selected טקסט"),
        )
    }

    @Test fun unknownAction_isNull() {
        assertNull(ShareIntentParser.resolve(Intent.ACTION_VIEW, "text/plain", "abc", null))
    }
}
