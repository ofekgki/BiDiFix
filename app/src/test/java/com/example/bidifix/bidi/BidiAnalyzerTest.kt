package com.example.bidifix.bidi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests for [BidiTextAnalyzer], [BidiCharacters] and the debug helpers. */
class BidiAnalyzerTest {

    @Test fun isHebrew_recognisesHebrewBlock() {
        assertTrue(BidiTextAnalyzer.isHebrew('ש'))
        assertTrue(BidiTextAnalyzer.isHebrew('א'))
        assertFalse(BidiTextAnalyzer.isHebrew('A'))
        assertFalse(BidiTextAnalyzer.isHebrew('5'))
    }

    @Test fun directionOf_classifiesStrongAndWeak() {
        assertEquals(Direction.RTL, BidiTextAnalyzer.directionOf('ש'))
        assertEquals(Direction.LTR, BidiTextAnalyzer.directionOf('A'))
        assertEquals(Direction.NUMBER, BidiTextAnalyzer.directionOf('7'))
        assertEquals(Direction.NEUTRAL, BidiTextAnalyzer.directionOf(' '))
        assertEquals(Direction.NEUTRAL, BidiTextAnalyzer.directionOf(','))
    }

    @Test fun categoryOf_coversRequiredCategories() {
        assertEquals(CharCategory.HEBREW, BidiTextAnalyzer.categoryOf('ש'))
        assertEquals(CharCategory.LATIN, BidiTextAnalyzer.categoryOf('A'))
        assertEquals(CharCategory.NUMBER, BidiTextAnalyzer.categoryOf('9'))
        assertEquals(CharCategory.LINE_BREAK, BidiTextAnalyzer.categoryOf('\n'))
        assertEquals(CharCategory.WHITESPACE, BidiTextAnalyzer.categoryOf(' '))
        assertEquals(CharCategory.OPEN_BRACKET, BidiTextAnalyzer.categoryOf('('))
        assertEquals(CharCategory.CLOSE_BRACKET, BidiTextAnalyzer.categoryOf(')'))
        assertEquals(CharCategory.COMMA, BidiTextAnalyzer.categoryOf(','))
        assertEquals(CharCategory.PERIOD, BidiTextAnalyzer.categoryOf('.'))
        assertEquals(CharCategory.COLON, BidiTextAnalyzer.categoryOf(':'))
        assertEquals(CharCategory.SEMICOLON, BidiTextAnalyzer.categoryOf(';'))
        assertEquals(CharCategory.HYPHEN, BidiTextAnalyzer.categoryOf('-'))
        assertEquals(CharCategory.SLASH, BidiTextAnalyzer.categoryOf('/'))
        assertEquals(CharCategory.APOSTROPHE, BidiTextAnalyzer.categoryOf('\''))
        assertEquals(CharCategory.QUOTE, BidiTextAnalyzer.categoryOf('"'))
        assertEquals(CharCategory.BIDI_CONTROL, BidiTextAnalyzer.categoryOf(BidiCharacters.LRI))
    }

    @Test fun firstStrongDirection_followsUbaP2() {
        assertEquals(Direction.RTL, BidiTextAnalyzer.firstStrongDirection("  123 שלום A"))
        assertEquals(Direction.LTR, BidiTextAnalyzer.firstStrongDirection("  123 Hello ש"))
        assertEquals(null, BidiTextAnalyzer.firstStrongDirection("123 :-) 45"))
    }

    @Test fun bidiCharacters_isBidiControlAndNames() {
        assertTrue(BidiCharacters.isBidiControl(BidiCharacters.LRI))
        assertTrue(BidiCharacters.isBidiControl(BidiCharacters.RLM))
        assertFalse(BidiCharacters.isBidiControl('A'))
        assertEquals("LRI", BidiCharacters.nameOf(BidiCharacters.LRI))
        assertEquals("PDI", BidiCharacters.nameOf(BidiCharacters.PDI))
    }

    @Test fun debugRenderers_areReadable() {
        val s = "Hi${BidiCharacters.LRI}x${BidiCharacters.PDI}"
        assertEquals("Hi[LRI]x[PDI]", s.toDebugString())
        assertEquals("U+0041 U+0042", "AB".toUnicodeDebugString())
    }
}
