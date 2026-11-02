package com.notas.personal.data

import androidx.room.withTransaction

object NoteRules {
    const val TOPIC_MAX_LENGTH = 80
    const val NOTE_MAX_LENGTH = 20_000
    fun topicName(value: String): String = value.trim().also {
        require(it.isNotBlank()) { "Escribí un nombre para la temática." }
        require(it.length <= TOPIC_MAX_LENGTH) { "El nombre admite hasta 80 caracteres." }
        require(!it.contains('\n') && !it.contains('\r')) { "Usá una sola línea para el nombre." }
    }
    fun noteBody(value: String): String = value.trim().also {
        require(it.isNotBlank()) { "Escribí algo antes de guardar la nota." }
        require(it.length <= NOTE_MAX_LENGTH) { "La nota admite hasta 20.000 caracteres." }
    }
}

class NotesRepository(private val database: NotesDatabase, private val clock: () -> Long = System::currentTimeMillis) {
    private val dao = database.notesDao()
    val topics = dao.observeTopics()
    fun notes(topicId: Long) = dao.observeNotes(topicId)

    suspend fun createTopic(name: String): Long {
        val cleanName = NoteRules.topicName(name)
        val now = clock()
        return dao.insertTopic(Topic(name = cleanName, createdAt = now, updatedAt = now))
    }

    suspend fun renameTopic(id: Long, name: String) {
        val cleanName = NoteRules.topicName(name)
        database.withTransaction {
            val topic = requireNotNull(dao.getTopic(id)) { "La temática ya no está disponible." }
            dao.updateTopic(topic.copy(name = cleanName, updatedAt = clock()))
        }
    }

    suspend fun addNote(topicId: Long, body: String): Long {
        val cleanBody = NoteRules.noteBody(body)
        return database.withTransaction {
            val topic = requireNotNull(dao.getTopic(topicId)) { "La temática ya no está disponible." }
            val now = clock()
            val id = dao.insertNote(Note(topicId = topicId, body = cleanBody, createdAt = now, updatedAt = now))
            dao.updateTopic(topic.copy(updatedAt = now))
            id
        }
    }

    suspend fun editNote(id: Long, body: String) {
        val cleanBody = NoteRules.noteBody(body)
        database.withTransaction {
            val note = requireNotNull(dao.getNote(id)) { "La nota ya no está disponible." }
            val topic = requireNotNull(dao.getTopic(note.topicId))
            val now = clock()
            dao.updateNote(note.copy(body = cleanBody, updatedAt = now))
            dao.updateTopic(topic.copy(updatedAt = now))
        }
    }

    suspend fun deleteNote(id: Long) {
        database.withTransaction {
            val note = requireNotNull(dao.getNote(id)) { "La nota ya no está disponible." }
            val topic = requireNotNull(dao.getTopic(note.topicId))
            dao.deleteNote(id)
            dao.updateTopic(topic.copy(updatedAt = clock()))
        }
    }
}
