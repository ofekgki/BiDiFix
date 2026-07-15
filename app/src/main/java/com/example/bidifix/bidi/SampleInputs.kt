package com.example.bidifix.bidi

/** Canonical mixed-direction examples used in previews and unit tests. */
object SampleInputs {
    val ALL: List<String> = listOf(
        "האפליקציה נכתבה באמצעות Jetpack Compose.",
        "לחץ על \"Copy to Clipboard\" כדי להעתיק.",
        "יש להפעיל את הפונקציה transformText(input) לפני השמירה.",
        "המערכת כוללת API, Database, Backend ו-Android App.",
        "פתח את הקובץ MainActivity.kt בתיקייה app/src/main/java.",
        "למידע נוסף בקר באתר https://developer.android.com.",
        "שלח הודעה אל test@example.com עד השעה 18:30.",
        "הגרסה הנוכחית היא version-2.1.4 והיא כוללת 25 תיקונים.",
        "Use the message \"הטקסט הועתק בהצלחה\" after copying.",
        "השתמש בערך (defaultValue) כאשר אין קלט.",
    )

    /** A short default shown when the app first opens. */
    val DEFAULT: String = ALL.first()
}
