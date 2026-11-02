package com.notas.personal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notas.personal.data.NotesRepository
import com.notas.personal.data.TopicSummary
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class NotesViewModel(private val repository: NotesRepository, private val state: SavedStateHandle) : ViewModel() {
    val selectedTopic = state.getStateFlow<Long?>("topic", null)
    private val errors = Channel<String>(Channel.BUFFERED)
    val topics = repository.topics.map<List<TopicSummary>, List<TopicSummary>?> { it }
        .catch { errors.send("No pudimos leer tus notas. Cerrá y volvé a abrir la app."); emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val messages = errors.receiveAsFlow()
    fun openTopic(id: Long?) { state["topic"] = id }
    fun notes(id: Long) = repository.notes(id)
    suspend fun createTopic(name: String): Boolean = perform { openTopic(repository.createTopic(name)) }
    suspend fun renameTopic(id: Long, name: String) = perform { repository.renameTopic(id, name) }
    suspend fun addNote(topicId: Long, body: String) = perform { repository.addNote(topicId, body) }
    suspend fun editNote(id: Long, body: String) = perform { repository.editNote(id, body) }
    suspend fun deleteNote(id: Long) = perform { repository.deleteNote(id) }
    private suspend fun perform(operation: suspend () -> Unit): Boolean = try {
        operation()
        true
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        errors.send(if (error is IllegalArgumentException) error.message ?: "Revisá el contenido." else "No se pudo guardar el cambio. Tu texto se conserva; intentá de nuevo.")
        false
    }
}
