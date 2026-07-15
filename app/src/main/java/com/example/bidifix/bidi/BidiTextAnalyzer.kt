package com.example.bidifix.bidi

/** Resolved directionality of a character or text run. */
enum class Direction {
    /** Strong right-to-left (e.g. Hebrew). */
    RTL,

    /** Strong left-to-right (e.g. Latin). */
    LTR,

    /** European/Arabic number (weak, laid out left-to-right). */
    NUMBER,

    /** Whitespace, punctuation, symbols and other neutral characters. */
    NEUTRAL,
}

/**
 * Fine-grained character categories. The transformer itself resolves direction through
 * [BidiTextAnalyzer.directionOf], but these categories make the intent of the parsing
 * explicit and are exercised directly by the unit tests.
 */
enum class CharCategory {
    HEBREW,
    LATIN,
    NUMBER,
    WHITESPACE,
    LINE_BREAK,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    COMMA,
    PERIOD,
    COLON,
    SEMICOLON,
    HYPHEN,
    SLASH,
    APOSTROPHE,
    QUOTE,
    MATH_SYMBOL,
    EMOJI,
    BIDI_CONTROL,
    OTHER_NEUTRAL,
}

/**
 * Classifies characters and resolves the base direction of paragraphs using
 * [Character.getDirectionality] as the authoritative Unicode source. This class is pure
 * JVM logic with no Android dependencies so it can be unit-tested in isolation.
 */
object BidiTextAnalyzer {

    private val OPEN_BRACKETS = setOf('(', '[', '{', '<')
    private val CLOSE_BRACKETS = setOf(')', ']', '}', '>')
    private val QUOTES = setOf('"', '\'', '“', '”', '‘', '’', '״', '׳')
    private val APOSTROPHES = setOf('\'', '’', '׳')

    /** True when the code point lies inside the Hebrew or Hebrew-presentation blocks. */
    fun isHebrew(c: Char): Boolean {
        val code = c.code
        return code in 0x0590..0x05FF || code in 0xFB1D..0xFB4F
    }

    /** Strong/weak direction of a single character. */
    fun directionOf(c: Char): Direction {
        if (BidiCharacters.isBidiControl(c)) return Direction.NEUTRAL
        return when (Character.getDirectionality(c)) {
            Character.DIRECTIONALITY_LEFT_TO_RIGHT -> Direction.LTR
            Character.DIRECTIONALITY_RIGHT_TO_LEFT,
            Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC -> Direction.RTL

            Character.DIRECTIONALITY_EUROPEAN_NUMBER,
            Character.DIRECTIONALITY_ARABIC_NUMBER -> Direction.NUMBER

            else -> Direction.NEUTRAL
        }
    }

    /** Detailed category used for tokenisation and documentation. */
    fun categoryOf(c: Char): CharCategory = when {
        c == '\n' || c == '\r' -> CharCategory.LINE_BREAK
        BidiCharacters.isBidiControl(c) -> CharCategory.BIDI_CONTROL
        isHebrew(c) -> CharCategory.HEBREW
        c in OPEN_BRACKETS -> CharCategory.OPEN_BRACKET
        c in CLOSE_BRACKETS -> CharCategory.CLOSE_BRACKET
        c == ',' -> CharCategory.COMMA
        c == '.' -> CharCategory.PERIOD
        c == ':' -> CharCategory.COLON
        c == ';' -> CharCategory.SEMICOLON
        c == '-' || c == '–' || c == '—' -> CharCategory.HYPHEN
        c == '/' || c == '\\' -> CharCategory.SLASH
        c in APOSTROPHES -> CharCategory.APOSTROPHE
        c in QUOTES -> CharCategory.QUOTE
        Character.isWhitespace(c) -> CharCategory.WHITESPACE
        Character.isDigit(c) || directionOf(c) == Direction.NUMBER -> CharCategory.NUMBER
        directionOf(c) == Direction.LTR && Character.isLetter(c) -> CharCategory.LATIN
        Character.getType(c) == Character.MATH_SYMBOL.toInt() -> CharCategory.MATH_SYMBOL
        isEmoji(c) -> CharCategory.EMOJI
        else -> CharCategory.OTHER_NEUTRAL
    }

    /**
     * Base direction of a paragraph, defined by its first strong directional character
     * (UBA rule P2/P3). Returns `null` when the paragraph has no strong character, in
     * which case the caller should leave the paragraph untouched.
     */
    fun firstStrongDirection(text: String): Direction? {
        for (c in text) {
            when (directionOf(c)) {
                Direction.LTR -> return Direction.LTR
                Direction.RTL -> return Direction.RTL
                else -> {} // numbers and neutrals are not "strong"
            }
        }
        return null
    }

    private fun isEmoji(c: Char): Boolean {
        if (Character.isSurrogate(c)) return true
        return when (Character.getType(c)) {
            Character.OTHER_SYMBOL.toInt() -> true
            else -> false
        }
    }
}
