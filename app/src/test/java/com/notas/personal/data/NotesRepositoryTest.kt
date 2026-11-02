package com.notas.personal.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30, 35])
class NotesRepositoryTest {
    private lateinit var context: Context
    private lateinit var database: NotesDatabase
    private lateinit var repository: NotesRepository
    private var now = 1_000L
    private val filename = "repository-test.db"

    @Before fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(filename)
        openDatabase()
    }
    private fun openDatabase() {
        database = NotesDatabase.open(context, filename)
        repository = NotesRepository(database) { now }
    }
    @After fun tearDown() { database.close(); context.deleteDatabase(filename) }

    @Test fun notesSurviveDatabaseCloseAndReopen() = runBlocking {
        val topic = repository.createTopic("Películas y series")
        repository.addNote(topic, "Ver esta película el sábado 🎬\nCon subtítulos")
        database.close()
        openDatabase()
        val restored = repository.notes(topic).first()
        assertEquals(1, restored.size)
        assertEquals("Ver esta película el sábado 🎬\nCon subtítulos", restored.single().body)
        assertEquals("Películas y series", repository.topics.first().single().topic.name)
    }

    @Test fun editPreservesIdentityAndChronologicalPosition() = runBlocking {
        val topic = repository.createTopic("Ideas")
        val first = repository.addNote(topic, "Primera")
        now = 2_000
        val second = repository.addNote(topic, "Segunda")
        now = 3_000
        repository.editNote(first, "Primera corregida")
        val notes = repository.notes(topic).first()
        assertEquals(listOf(first, second), notes.map { it.id })
        assertEquals(1_000L, notes.first().createdAt)
        assertEquals(3_000L, notes.first().updatedAt)
        assertEquals("Segunda", repository.topics.first().single().preview)
    }

    @Test fun deletionUpdatesPreviewAndKeepsOtherTopicsUntouched() = runBlocking {
        val firstTopic = repository.createTopic("Rutina")
        val otherTopic = repository.createTopic("Rutina")
        repository.addNote(firstTopic, "Primera nota")
        val removed = repository.addNote(firstTopic, "Última nota")
        repository.addNote(otherTopic, "Otra temática")
        repository.deleteNote(removed)
        assertEquals(listOf("Primera nota"), repository.notes(firstTopic).first().map { it.body })
        assertEquals("Otra temática", repository.notes(otherTopic).first().single().body)
        val summary = repository.topics.first().first { it.topic.id == firstTopic }
        assertEquals("Primera nota", summary.preview)
        assertEquals(1, summary.noteCount)
    }

    @Test fun renameKeepsNotesAndActivityChangesOrder() = runBlocking {
        val older = repository.createTopic("  Ideas  ")
        now = 2_000
        val newer = repository.createTopic("Proyectos")
        assertEquals(listOf(newer, older), repository.topics.first().map { it.topic.id })
        now = 3_000
        repository.addNote(older, "Guardar idea")
        now = 4_000
        repository.renameTopic(older, "Inspiración")
        assertEquals(older, repository.topics.first().first().topic.id)
        assertEquals("Inspiración", repository.topics.first().first().topic.name)
        assertEquals("Guardar idea", repository.notes(older).first().single().body)
    }

    @Test fun invalidInputAndMissingParentNeverCreatePartialData() = runBlocking {
        try { repository.createTopic("  \n "); fail("Debía rechazar el nombre vacío") } catch (_: IllegalArgumentException) { }
        val topic = repository.createTopic("Válida")
        try { repository.addNote(topic, "\n \t"); fail("Debía rechazar texto vacío") } catch (_: IllegalArgumentException) { }
        try { repository.addNote(999, "Sin temática"); fail("Debía rechazar temática inexistente") } catch (_: IllegalArgumentException) { }
        assertTrue(repository.notes(topic).first().isEmpty())
        assertEquals(1, repository.topics.first().size)
        assertNull(repository.topics.first().single().preview)
    }
}
