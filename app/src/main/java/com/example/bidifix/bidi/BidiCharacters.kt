package com.example.bidifix.bidi

/**
 * Constants and helpers for the invisible Unicode bidirectional formatting characters.
 *
 * These characters carry no visible glyph; they only influence how the Unicode
 * Bidirectional Algorithm (UBA) lays out surrounding text. The transformer inserts
 * them into the output string so the corrected direction "travels" with the text when
 * it is copied into another application.
 *
 * Prefer the *isolate* characters ([LRI], [RLI], [FSI] / [PDI]) for new formatting:
 * an isolate treats its content as a single neutral object, so the direction of the
 * wrapped run cannot leak into the text around it. The older *embedding* and
 * *override* characters ([LRE], [RLE], [PDF], [LRO], [RLO]) are kept for completeness
 * and for round-tripping input that already contains them, but the transformer never
 * emits overrides because they can reorder text unexpectedly.
 */
object BidiCharacters {

    // --- Implicit marks (zero-width strong characters) ---
    const val LRM = '‎' // Left-to-Right Mark
    const val RLM = '‏' // Right-to-Left Mark

    // --- Embeddings (legacy) ---
    const val LRE = '‪' // Left-to-Right Embedding
    const val RLE = '‫' // Right-to-Left Embedding
    const val PDF = '‬' // Pop Directional Formatting

    // --- Overrides (legacy, avoid) ---
    const val LRO = '‭' // Left-to-Right Override
    const val RLO = '‮' // Right-to-Left Override

    // --- Isolates (preferred) ---
    const val LRI = '⁦' // Left-to-Right Isolate
    const val RLI = '⁧' // Right-to-Left Isolate
    const val FSI = '⁨' // First Strong Isolate
    const val PDI = '⁩' // Pop Directional Isolate

    /** Human readable label for each control, used by the debug renderer. */
    private val labels: Map<Char, String> = mapOf(
        LRM to "LRM",
        RLM to "RLM",
        LRE to "LRE",
        RLE to "RLE",
        PDF to "PDF",
        LRO to "LRO",
        RLO to "RLO",
        LRI to "LRI",
        RLI to "RLI",
        FSI to "FSI",
        PDI to "PDI",
    )

    /** Every control character managed or recognised by this module. */
    val ALL: Set<Char> = labels.keys

    /**
     * Controls this module actively inserts and re-normalises. Embeddings and overrides
     * are intentionally excluded so that input which already uses them is preserved.
     */
    val MANAGED: Set<Char> = setOf(LRM, RLM, LRI, RLI, FSI, PDI)

    /** The three isolate initiators. */
    val ISOLATE_INITIATORS: Set<Char> = setOf(LRI, RLI, FSI)

    /** Embedding and override initiators (require a matching [PDF]). */
    val EMBEDDING_INITIATORS: Set<Char> = setOf(LRE, RLE, LRO, RLO)

    /** True when [c] is any Unicode bidirectional formatting control. */
    fun isBidiControl(c: Char): Boolean = c in ALL

    /** True when [c] opens an isolate ([LRI], [RLI] or [FSI]). */
    fun isIsolateInitiator(c: Char): Boolean = c in ISOLATE_INITIATORS

    /** Readable name for a control, or the raw code point (e.g. `U+2066`) if unknown. */
    fun nameOf(c: Char): String = labels[c] ?: "U+%04X".format(c.code)
}

/**
 * Debug-only rendering that replaces invisible controls with bracketed labels so they
 * can be seen in logs and test failures, e.g. `Hello[LRI]world[PDI][RLM].`.
 *
 * This representation is for development only and must never be copied to the clipboard
 * or shown as normal output.
 */
fun String.toDebugString(): String {
    val sb = StringBuilder(length + 8)
    for (c in this) {
        if (BidiCharacters.isBidiControl(c)) {
            sb.append('[').append(BidiCharacters.nameOf(c)).append(']')
        } else {
            sb.append(c)
        }
    }
    return sb.toString()
}

/**
 * Renders the exact Unicode code points of a string, e.g. `U+05E9 U+05DC U+2066`.
 * Useful in unit tests to assert on invisible characters that are impossible to see.
 */
fun String.toUnicodeDebugString(): String =
    codePoints().toArray().joinToString(" ") { "U+%04X".format(it) }
