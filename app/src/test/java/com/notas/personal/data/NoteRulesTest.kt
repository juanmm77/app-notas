package com.notas.personal.data

import org.junit.Assert.*
import org.junit.Test

class NoteRulesTest {
    @Test fun retainsMultilineUnicodeContent() {
        assertEquals("Idea 💡\nSegunda línea", NoteRules.noteBody("  Idea 💡\nSegunda línea  "))
    }
    @Test fun acceptsExactLimits() {
        assertEquals(80, NoteRules.topicName("a".repeat(80)).length)
        assertEquals(20_000, NoteRules.noteBody("a".repeat(20_000)).length)
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsOversizedNotes() { NoteRules.noteBody("a".repeat(20_001)) }
    @Test(expected = IllegalArgumentException::class) fun rejectsOversizedTopics() { NoteRules.topicName("a".repeat(81)) }
    @Test(expected = IllegalArgumentException::class) fun rejectsMultilineNames() { NoteRules.topicName("Uno\nDos") }
}
