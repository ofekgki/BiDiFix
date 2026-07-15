package com.example.bidifix.bidi

import com.example.bidifix.bidi.BidiCharacters.LRI
import com.example.bidifix.bidi.BidiCharacters.LRM
import com.example.bidifix.bidi.BidiCharacters.PDI
import com.example.bidifix.bidi.BidiCharacters.RLI
import com.example.bidifix.bidi.BidiCharacters.RLM

/**
 * Inserts invisible Unicode isolate and mark characters so that mixed Hebrew/English
 * text renders correctly, without ever altering the visible characters.
 *
 * The algorithm is a pragmatic, token-oriented application of the Unicode Bidirectional
 * Algorithm:
 *
 *  1. Existing managed controls are stripped so the transform is idempotent.
 *  2. The text is split into paragraphs, preserving the exact line breaks.
 *  3. Each paragraph's base direction is taken from its first strong character.
 *  4. Every character is classified (strong LTR/RTL, number, or neutral).
 *  5. Numbers that belong to a Latin token (URLs, versions, ...) are folded into it,
 *     while standalone numbers stay weak.
 *  6. Neutral characters are resolved to the surrounding direction, or to the base
 *     direction when they sit between opposite runs.
 *  7. Maximal runs whose direction is opposite to the base are wrapped in a matching
 *     isolate ([LRI]/[RLI] .. [PDI]).
 *  8. A terminal [LRM]/[RLM] is added after a trailing opposite-direction isolate that
 *     is followed only by neutral punctuation, so the punctuation stays at the visual
 *     end of the sentence.
 *
 * It never reverses text, never touches spelling, and never relies on the UI's
 * `textDirection`: the correction lives in the returned string itself.
 */
class BidiTransformer {

    // Internal character direction codes (kept as ints for tight inner loops).
    private companion object {
        const val NEU = 0
        const val LTR = 1
        const val RTL = 2
        const val NUM = 3
    }

    /** Transforms [input], returning a new string with balanced direction controls. */
    fun transform(input: String): String {
        if (input.isEmpty()) return input

        val cleaned = removeRedundantControls(input)
        val out = StringBuilder(cleaned.length + 16)

        var i = 0
        val n = cleaned.length
        while (i < n) {
            val c = cleaned[i]
            if (c == '\n' || c == '\r') {
                // Preserve line breaks (including CRLF) exactly as separators.
                out.append(c)
                if (c == '\r' && i + 1 < n && cleaned[i + 1] == '\n') {
                    out.append('\n')
                    i++
                }
                i++
            } else {
                val start = i
                while (i < n && cleaned[i] != '\n' && cleaned[i] != '\r') i++
                out.append(transformParagraph(cleaned.substring(start, i)))
            }
        }
        return out.toString()
    }

    private fun transformParagraph(p: String): String {
        val n = p.length
        if (n == 0) return p

        val base = when (BidiTextAnalyzer.firstStrongDirection(p)) {
            Direction.LTR -> LTR
            Direction.RTL -> RTL
            else -> return p // no strong character: leave untouched
        }

        // Step 1: raw per-character class.
        val cls = IntArray(n) { rawClass(p[it]) }

        // Step 2: fold numbers into Latin tokens. Within each whitespace-delimited
        // segment, if any strong LTR letter is present, its numbers become LTR so
        // that identifiers/URLs/versions stay atomic. Otherwise numbers stay weak.
        foldNumbersIntoLatin(p, cls)

        // Step 2b: pair matched brackets (UBA rule N0). A bracket pair whose inner content
        // is opposite to the base direction takes that opposite direction on BOTH brackets,
        // so the pair stays together inside one isolate instead of being split.
        resolveBracketPairs(p, cls, base)

        // Step 3: resolve every neutral / leftover number to a strong direction.
        val resolved = IntArray(n)
        for (k in 0 until n) {
            resolved[k] = when (cls[k]) {
                LTR -> LTR
                RTL -> RTL
                else -> resolveNeutral(cls, k, base)
            }
        }

        // Step 4: group maximal same-direction runs and wrap opposite runs.
        val sb = StringBuilder(n + 8)
        var i = 0
        while (i < n) {
            val d = resolved[i]
            var j = i
            while (j < n && resolved[j] == d) j++
            val segment = p.substring(i, j)
            if (d == base) {
                sb.append(segment)
            } else {
                sb.append(if (d == LTR) LRI else RLI).append(segment).append(PDI)
            }
            i = j
        }

        return appendTerminalMark(sb.toString(), base)
    }

    private fun rawClass(c: Char): Int = when (BidiTextAnalyzer.directionOf(c)) {
        Direction.LTR -> LTR
        Direction.RTL -> RTL
        Direction.NUMBER -> NUM
        Direction.NEUTRAL -> NEU
    }

    private fun foldNumbersIntoLatin(p: String, cls: IntArray) {
        val n = p.length
        var i = 0
        while (i < n) {
            if (Character.isWhitespace(p[i])) {
                i++
                continue
            }
            val start = i
            while (i < n && !Character.isWhitespace(p[i])) i++
            var hasLatin = false
            for (k in start until i) if (cls[k] == LTR) {
                hasLatin = true
                break
            }
            if (hasLatin) {
                for (k in start until i) if (cls[k] == NUM) cls[k] = LTR
            }
        }
    }

    /**
     * Assigns matched bracket pairs a shared strong direction so they are never split
     * across an isolate boundary. For each pair, if the inside contains the base direction
     * it stays base; otherwise if it contains the opposite direction, both brackets take
     * that opposite direction (and so join the wrapped run).
     */
    private fun resolveBracketPairs(p: String, cls: IntArray, base: Int) {
        val openers = "([{<"
        val closers = ")]}>"
        val opp = if (base == LTR) RTL else LTR
        val openIdx = ArrayDeque<Int>()
        for (i in p.indices) {
            val c = p[i]
            when {
                c in openers -> openIdx.addLast(i)
                c in closers && openIdx.isNotEmpty() &&
                    openers[closers.indexOf(c)] == p[openIdx.last()] -> {
                    val open = openIdx.removeLast()
                    var hasBase = false
                    var hasOpp = false
                    for (k in open + 1 until i) {
                        if (cls[k] == base) hasBase = true else if (cls[k] == opp) hasOpp = true
                    }
                    val forced = when {
                        hasBase -> base
                        hasOpp -> opp
                        else -> 0
                    }
                    if (forced != 0) {
                        cls[open] = forced
                        cls[i] = forced
                    }
                }
            }
        }
    }

    /** Resolves the direction of a neutral (or standalone number) at index [k]. */
    private fun resolveNeutral(cls: IntArray, k: Int, base: Int): Int {
        var prev = 0
        var j = k - 1
        while (j >= 0) {
            if (cls[j] == LTR || cls[j] == RTL) {
                prev = cls[j]
                break
            }
            j--
        }
        var next = 0
        var m = k + 1
        while (m < cls.size) {
            if (cls[m] == LTR || cls[m] == RTL) {
                next = cls[m]
                break
            }
            m++
        }
        return if (prev != 0 && prev == next) prev else base
    }

    /**
     * If the paragraph ends with an opposite-direction isolate followed only by neutral
     * terminal punctuation, insert the base mark so the punctuation is anchored to the
     * base direction and renders at the visual end of the sentence.
     */
    private fun appendTerminalMark(text: String, base: Int): String {
        if (text.isEmpty()) return text
        var end = text.length
        // Allow trailing whitespace after the punctuation.
        while (end > 0 && Character.isWhitespace(text[end - 1])) end--
        var punctStart = end
        while (punctStart > 0 && isTerminalPunctuation(text[punctStart - 1])) punctStart--
        if (punctStart == end || punctStart == 0) return text // no trailing punctuation
        if (text[punctStart - 1] != PDI) return text // must directly follow an isolate
        val mark = if (base == RTL) RLM else LRM
        return buildString {
            append(text, 0, punctStart)
            append(mark)
            append(text, punctStart, text.length)
        }
    }

    private fun isTerminalPunctuation(c: Char): Boolean =
        c == '.' || c == ',' || c == ';' || c == ':' || c == '!' || c == '?'

    // --- Existing-control handling -------------------------------------------------

    /**
     * Removes the controls this transformer manages ([LRM], [RLM], [LRI], [RLI], `FSI`,
     * [PDI]) so re-running the transform does not accumulate formatting. Embedding and
     * override controls are deliberately left in place.
     */
    fun removeRedundantControls(text: String): String {
        if (text.none { it in BidiCharacters.MANAGED }) return text
        val sb = StringBuilder(text.length)
        for (c in text) if (c !in BidiCharacters.MANAGED) sb.append(c)
        return sb.toString()
    }

    /**
     * Verifies that all isolate and embedding/override controls are correctly nested and
     * balanced. Returns `false` for any unmatched initiator or terminator.
     */
    fun validateBalancedControls(text: String): Boolean {
        var isolateDepth = 0
        var embeddingDepth = 0
        for (c in text) {
            when {
                BidiCharacters.isIsolateInitiator(c) -> isolateDepth++
                c == PDI -> {
                    if (isolateDepth == 0) return false
                    isolateDepth--
                }
                c in BidiCharacters.EMBEDDING_INITIATORS -> embeddingDepth++
                c == BidiCharacters.PDF -> {
                    if (embeddingDepth == 0) return false
                    embeddingDepth--
                }
            }
        }
        return isolateDepth == 0 && embeddingDepth == 0
    }
}
