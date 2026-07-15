package com.example.bidifix.bidi

import com.example.bidifix.bidi.BidiCharacters.LRI
import com.example.bidifix.bidi.BidiCharacters.PDI
import com.example.bidifix.bidi.BidiCharacters.RLI
import com.example.bidifix.bidi.BidiCharacters.RLM
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Behavioural tests for [BidiTransformer]. Assertions compare exact code points (via the
 * control constants) rather than rendered glyphs, and use [codePointsDebug] /
 * [controlsDebug] in failure messages.
 */
class BidiTransformerTest {

    private val transformer = BidiTransformer()

    private fun lri(run: String) = "$LRI$run$PDI"
    private fun rli(run: String) = "$RLI$run$PDI"

    private fun assertContains(needle: String, haystack: String) {
        assertTrue(
            "expected to contain <${needle.controlsDebug()}>\n" +
                "actual: <${haystack.controlsDebug()}>\n" +
                "points: ${haystack.codePointsDebug()}",
            haystack.contains(needle),
        )
    }

    // 1. Plain Hebrew is untouched (visually and logically valid already).
    @Test fun plainHebrew_isUnchanged() {
        val input = "שלום עולם כאן"
        assertEquals(input, transformer.transform(input))
    }

    // 2. Plain English is not unnecessarily modified.
    @Test fun plainEnglish_isUnchanged() {
        val input = "Hello world here"
        assertEquals(input, transformer.transform(input))
    }

    // 3. English inside Hebrew is isolated with LRI..PDI.
    @Test fun englishInsideHebrew_isIsolated() {
        val out = transformer.transform("יש להפעיל transformText לפני השמירה")
        assertContains(lri("transformText"), out)
    }

    // 4. Hebrew inside English is isolated with RLI..PDI.
    @Test fun hebrewInsideEnglish_isIsolated() {
        val out = transformer.transform("Please click כאן now")
        assertContains(rli("כאן"), out)
    }

    // 5. A comma-separated English list stays one isolated run so commas stay attached.
    @Test fun commaSeparatedEnglishList_isOneRun() {
        val out = transformer.transform("המערכת כוללת API, Database, Backend ו-Android App.")
        assertContains(lri("API, Database, Backend"), out)
    }

    // 6. A final period lands at the sentence end, anchored by an RLM.
    @Test fun finalPeriod_isAnchoredWithRlm() {
        val out = transformer.transform("האפליקציה נכתבה באמצעות Jetpack Compose.")
        assertContains(lri("Jetpack Compose"), out)
        assertTrue(
            "should end with PDI+RLM+'.': ${out.controlsDebug()}",
            out.endsWith("$PDI$RLM."),
        )
    }

    // 7. The complete bracketed English expression is wrapped as one run inside Hebrew.
    @Test fun parenthesesAroundEnglishInHebrew() {
        val out = transformer.transform("השתמש בערך (defaultValue) כאשר אין קלט.")
        assertContains(lri("(defaultValue)"), out)
    }

    // 8. The complete bracketed Hebrew expression is wrapped as one run inside English.
    @Test fun parenthesesAroundHebrewInEnglish() {
        val out = transformer.transform("Use the value (ערך ברירת מחדל) when no input exists")
        assertContains(rli("(ערך ברירת מחדל)"), out)
    }

    // 8b. A matched bracket pair is never split across an isolate boundary (the reported
    // "GNU (Gnu's Not Unix)" bug: opening paren was wrapped but closing paren leaked out).
    @Test fun bracketPair_isNeverSplit() {
        val out = transformer.transform("انا أحب GNU (Gnu's Not Unix).")
        assertContains(lri("GNU (Gnu's Not Unix)"), out)
    }

    // 9. A quoted English phrase stays inside its quotes as one run.
    @Test fun quotedEnglishPhrase_isOneRun() {
        val out = transformer.transform("לחץ על \"Copy to Clipboard\" כדי להעתיק.")
        assertContains("\"${lri("Copy to Clipboard")}\"", out)
    }

    // 10. Apostrophes do not break contractions.
    @Test fun apostrophe_doesNotBreakContraction() {
        val out = transformer.transform("הוא אמר don't עכשיו")
        assertContains(lri("don't"), out)
    }

    // 11. URLs remain a single intact run (trailing period excluded).
    @Test fun url_isIntact() {
        val out = transformer.transform("למידע נוסף בקר באתר https://developer.android.com.")
        assertContains(lri("https://developer.android.com"), out)
    }

    // 12. Email addresses remain intact.
    @Test fun email_isIntact() {
        val out = transformer.transform("שלח הודעה אל test@example.com עד השעה 18:30.")
        assertContains(lri("test@example.com"), out)
    }

    // 13. Numbers and decimals: identifiers wrap, bare numbers do not.
    @Test fun numbers_bareNumbersAreNotWrapped() {
        val out = transformer.transform("הגרסה הנוכחית היא version-2.1.4 והיא כוללת 25 תיקונים.")
        assertContains(lri("version-2.1.4"), out)
        // "25" is a standalone number: present and NOT wrapped in an isolate.
        assertContains(" 25 ", out)
        assertFalse("bare number should not be isolated", out.contains(lri("25")))
    }

    // 14. Dates and times stay intact and unwrapped.
    @Test fun datesAndTimes_areIntact() {
        val date = transformer.transform("התאריך הוא 12/07/2026 בבוקר")
        assertContains(" 12/07/2026 ", date)
        assertFalse(date.contains(lri("12/07/2026")))

        val time = transformer.transform("הפגישה בשעה 18:30 בערב")
        assertContains(" 18:30 ", time)
    }

    // 15. Hebrew prefix such as ב-Android keeps the hyphen between prefix and term.
    @Test fun hebrewPrefixHyphenEnglish() {
        val out = transformer.transform("יש להתקין ב-Android מיד")
        assertContains("ב-${lri("Android")}", out)
    }

    // 16. Multiple paragraphs preserve the exact line breaks.
    @Test fun multipleParagraphs_preserveLineBreaks() {
        val input = "שלום Android\nHello עולם\n"
        val out = transformer.transform(input)
        assertEquals(
            "line-break count must be preserved",
            input.count { it == '\n' },
            out.count { it == '\n' },
        )
        // Each paragraph still isolates its opposite-direction run.
        assertContains(lri("Android"), out)
        assertContains(rli("עולם"), out)
    }

    // 17. Existing valid controls are not duplicated: pre-wrapped input == plain input.
    @Test fun existingControls_areNotDuplicated() {
        val plain = "יש להפעיל transformText לפני השמירה"
        val prewrapped = "יש להפעיל ${lri("transformText")} לפני השמירה"
        assertEquals(transformer.transform(plain), transformer.transform(prewrapped))
    }

    // 18. All generated controls are balanced for every sample.
    @Test fun allSamples_produceBalancedControls() {
        for (sample in SampleInputs.ALL) {
            val out = transformer.transform(sample)
            assertTrue(
                "unbalanced controls for <$sample>: ${out.controlsDebug()}",
                transformer.validateBalancedControls(out),
            )
        }
    }

    // 19. Empty input returns an empty string.
    @Test fun emptyInput_returnsEmpty() {
        assertEquals("", transformer.transform(""))
    }

    // 20. Transforming twice does not add more controls (idempotent).
    @Test fun transform_isIdempotent() {
        for (sample in SampleInputs.ALL) {
            val once = transformer.transform(sample)
            val twice = transformer.transform(once)
            assertEquals(
                "not idempotent for <$sample>\n" +
                    "once:  ${once.controlsDebug()}\n" +
                    "twice: ${twice.controlsDebug()}",
                once,
                twice,
            )
        }
    }

    // Cross-cutting: transformation never alters the visible characters.
    @Test fun visibleCharacters_arePreserved() {
        for (sample in SampleInputs.ALL) {
            assertEquals(
                "visible text changed for <$sample>",
                sample,
                transformer.transform(sample).visibleOnly(),
            )
        }
    }

    // Cross-cutting: removeRedundantControls strips exactly the managed controls.
    @Test fun removeRedundantControls_stripsManagedControls() {
        val wrapped = "a ${lri("b")} c$RLM"
        assertEquals("a b c", transformer.removeRedundantControls(wrapped))
    }

    // validateBalancedControls detects an unmatched isolate.
    @Test fun validate_detectsUnbalanced() {
        assertFalse(transformer.validateBalancedControls("abc${LRI}def"))
        assertTrue(transformer.validateBalancedControls("abc${lri("def")}"))
    }
}
