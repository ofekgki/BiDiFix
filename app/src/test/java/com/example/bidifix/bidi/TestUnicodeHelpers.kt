package com.example.bidifix.bidi

/**
 * Helpers for the test source set that make invisible control characters visible when an
 * assertion fails, so failures are debuggable.
 */

/** Space-separated code points, e.g. `U+05E9 U+05DC U+2066`. */
fun String.codePointsDebug(): String =
    codePoints().toArray().joinToString(" ") { "U+%04X".format(it) }

/** Bracketed control labels, e.g. `Hello[LRI]world[PDI]`. */
fun String.controlsDebug(): String = toDebugString()

/** The string with every bidi control removed — i.e. the visible characters only. */
fun String.visibleOnly(): String = filterNot { BidiCharacters.isBidiControl(it) }
