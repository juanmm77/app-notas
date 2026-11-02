package com.notas.personal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notas.personal.NotesViewModel
import com.notas.personal.data.*
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Spanish = Locale.forLanguageTag("es-AR")
private val HourFormat = DateTimeFormatter.ofPattern("HH:mm", Spanish)
private fun timeOf(value: Long) = Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault())
private fun dayLabel(value: Long): String {
    val date = timeOf(value).toLocalDate()
    return when (date) {
        LocalDate.now() -> "Hoy"
        LocalDate.now().minusDays(1) -> "Ayer"
        else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Spanish))
    }
}

@Composable
fun NotesApp(model: NotesViewModel) {
    val topics by model.topics.collectAsStateWithLifecycle()
    val selectedId by model.selectedTopic.collectAsStateWithLifecycle()
    val currentTopic = topics?.firstOrNull { it.topic.id == selectedId }
    val snackbar = remember { SnackbarHostState() }
    var creating by rememberSaveable { mutableStateOf(false) }
    var renaming by rememberSaveable { mutableStateOf(false) }
    BackHandler(selectedId != null) { model.openTopic(null) }
    LaunchedEffect(model) { model.messages.collect { snackbar.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { insets ->
        Box(Modifier.fillMaxSize().padding(insets).consumeWindowInsets(insets)) {
            when {
                topics == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                selectedId == null -> TopicList(topics.orEmpty(), { creating = true }, model::openTopic)
                currentTopic != null -> key(selectedId) {
                    val notesFlow = remember(selectedId) { model.notes(currentTopic.topic.id) }
                    val notes by notesFlow.collectAsStateWithLifecycle(initialValue = emptyList())
                    Conversation(currentTopic, notes, model, onBack = { model.openTopic(null) }, onRename = { renaming = true })
                }
                else -> EmptyPanel(Icons.Outlined.FolderOpen, "La temática no está disponible", "Volvé a tus temáticas para continuar.", "Volver", { model.openTopic(null) })
            }
        }
    }
    if (creating) {
        NameDialog("Nueva temática", "", onDismiss = { creating = false }) { name ->
            model.createTopic(name).also { if (it) creating = false }
        }
    }
    if (renaming && currentTopic != null) {
        NameDialog("Renombrar temática", currentTopic.topic.name, onDismiss = { renaming = false }) { name ->
            model.renameTopic(currentTopic.topic.id, name).also { if (it) renaming = false }
        }
    }
}

@Composable
private fun TopicList(topics: List<TopicSummary>, onCreate: () -> Unit, onOpen: (Long) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp, top = 24.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("TU ESPACIO PERSONAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Mis notas", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                    Icon(Icons.Outlined.AutoAwesome, null, Modifier.padding(14.dp).size(24.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Text("Un lugar para cada idea.", Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
            Row(Modifier.padding(horizontal = 24.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("TEMÁTICAS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Text(topics.size.toString(), Modifier.padding(horizontal = 9.dp, vertical = 2.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            if (topics.isEmpty()) {
                Box(Modifier.weight(1f)) {
                    EmptyPanel(Icons.Outlined.Forum, "Tus ideas empiezan acá", "Creá una temática para películas, proyectos o lo que quieras recordar.", "Crear mi primera temática", onCreate)
                }
            } else {
                LazyColumn(Modifier.weight(1f).testTag("topicList"), contentPadding = PaddingValues(bottom = 108.dp)) {
                    items(topics, key = { it.topic.id }) { summary ->
                        TopicRow(summary) { onOpen(summary.topic.id) }
                    }
                }
            }
        }
        if (topics.isNotEmpty()) {
            ExtendedFloatingActionButton(
                onClick = onCreate,
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                icon = { Icon(Icons.Outlined.Add, null) }, text = { Text("Nueva temática") },
                containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun TopicAvatar(id: Long, size: Int = 52) {
    val icons = listOf(Icons.Outlined.BookmarkBorder, Icons.Outlined.Lightbulb, Icons.Outlined.Spa, Icons.Outlined.LocalMovies, Icons.AutoMirrored.Outlined.MenuBook)
    val amber = id % 3 == 0L
    Surface(shape = RoundedCornerShape(18.dp), color = if (amber) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(size.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icons[((id - 1).coerceAtLeast(0) % icons.size).toInt()], null, Modifier.size((size / 2).dp), tint = if (amber) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun TopicRow(summary: TopicSummary, onClick: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            TopicAvatar(summary.topic.id)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(summary.topic.name, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.width(10.dp))
                    Text(if (dayLabel(summary.topic.updatedAt) == "Hoy") timeOf(summary.topic.updatedAt).format(HourFormat) else dayLabel(summary.topic.updatedAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(5.dp))
                Text(summary.preview?.replace('\n', ' ') ?: "Todavía no hay notas", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        HorizontalDivider(Modifier.padding(start = 90.dp, end = 24.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .65f))
    }
}

@Composable
private fun Conversation(summary: TopicSummary, notes: List<Note>, model: NotesViewModel, onBack: () -> Unit, onRename: () -> Unit) {
    var draft by rememberSaveable { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var actionNote by remember { mutableStateOf<Note?>(null) }
    var editNote by remember { mutableStateOf<Note?>(null) }
    var deleteNote by remember { mutableStateOf<Note?>(null) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LaunchedEffect(notes.lastOrNull()?.id) {
        if (notes.isNotEmpty()) listState.scrollToItem(notes.lastIndex)
    }
    Column(Modifier.fillMaxSize().imePadding()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver a temáticas") }
            TopicAvatar(summary.topic.id, 42)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(summary.topic.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${notes.size} ${if (notes.size == 1) "nota" else "notas"}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onRename) { Icon(Icons.Outlined.Edit, "Renombrar temática") }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        if (notes.isEmpty()) {
            Box(Modifier.weight(1f)) {
                EmptyPanel(Icons.Outlined.EditNote, "Una idea, un mensaje", "Escribí tu primera nota acá abajo. Este espacio es solo tuyo.")
            }
        } else {
            LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth().testTag("noteList"), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(notes.size, key = { notes[it].id }) { index ->
                    val note = notes[index]
                    if (index == 0 || timeOf(note.createdAt).toLocalDate() != timeOf(notes[index - 1].createdAt).toLocalDate()) {
                        Box(Modifier.fillMaxWidth().padding(bottom = 16.dp, top = if (index == 0) 0.dp else 12.dp), contentAlignment = Alignment.Center) {
                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Text(dayLabel(note.createdAt), Modifier.padding(horizontal = 12.dp, vertical = 5.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    NoteMessage(note) { actionNote = note }
                }
            }
        }
        Surface(color = MaterialTheme.colorScheme.background, tonalElevation = 1.dp) {
            Column {
                if (draft.length > NoteRules.NOTE_MAX_LENGTH) Text("Máximo 20.000 caracteres", Modifier.padding(start = 20.dp, top = 6.dp), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.Bottom) {
                    OutlinedTextField(
                        value = draft, onValueChange = { draft = it }, enabled = !saving,
                        placeholder = { Text("Escribí una nota…") },
                        modifier = Modifier.weight(1f).testTag("noteInput").semantics { contentDescription = "Texto de la nota" },
                        shape = RoundedCornerShape(26.dp), maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .45f)),
                    )
                    Spacer(Modifier.width(10.dp))
                    FilledIconButton(
                        onClick = { saving = true; scope.launch { try { if (model.addNote(summary.topic.id, draft)) draft = "" } finally { saving = false } } },
                        enabled = !saving && draft.isNotBlank() && draft.trim().length <= NoteRules.NOTE_MAX_LENGTH,
                        modifier = Modifier.size(56.dp), shape = CircleShape,
                    ) { if (saving) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) else Icon(Icons.AutoMirrored.Rounded.Send, "Guardar nota") }
                }
            }
        }
    }
    actionNote?.let { note ->
        AlertDialog(
            onDismissRequest = { actionNote = null }, title = { Text("Tu nota") },
            text = { Column {
                Text(note.body, maxLines = 3, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))
                TextButton(onClick = { actionNote = null; editNote = note }, Modifier.fillMaxWidth()) { Icon(Icons.Outlined.Edit, null); Spacer(Modifier.width(8.dp)); Text("Editar texto") }
                TextButton(onClick = { actionNote = null; deleteNote = note }, Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Icon(Icons.Outlined.DeleteOutline, null); Spacer(Modifier.width(8.dp)); Text("Eliminar nota") }
            } },
            confirmButton = { TextButton(onClick = { actionNote = null }) { Text("Cerrar") } },
        )
    }
    editNote?.let { note ->
        EditNoteDialog(note, onDismiss = { editNote = null }) { body -> model.editNote(note.id, body).also { if (it) editNote = null } }
    }
    deleteNote?.let { note ->
        var deleting by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!deleting) deleteNote = null },
            title = { Text("¿Eliminar esta nota?") },
            text = { Text("Se borrará definitivamente. Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(enabled = !deleting, onClick = { deleting = true; scope.launch { try { if (model.deleteNote(note.id)) deleteNote = null } finally { deleting = false } } }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Eliminar") } },
            dismissButton = { TextButton(enabled = !deleting, onClick = { deleteNote = null }) { Text("Cancelar") } },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteMessage(note: Note, onActions: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 5.dp, bottomStart = 20.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth(.88f).combinedClickable(onClick = {}, onLongClickLabel = "Opciones de la nota", onLongClick = onActions)
                .semantics { customActions = listOf(CustomAccessibilityAction("Editar o eliminar nota") { onActions(); true }) },
        ) {
            Column(Modifier.padding(start = 16.dp, end = 14.dp, top = 13.dp, bottom = 9.dp)) {
                Text(note.body, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    (if (note.updatedAt != note.createdAt) "editada · " else "") + timeOf(note.createdAt).format(HourFormat),
                    Modifier.align(Alignment.End).padding(top = 5.dp), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .72f),
                )
            }
        }
    }
}

@Composable
private fun NameDialog(title: String, initial: String, onDismiss: () -> Unit, onSave: suspend (String) -> Boolean) {
    var text by rememberSaveable { mutableStateOf(initial) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() }, title = { Text(title) },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, enabled = !busy, singleLine = true, label = { Text("Nombre") }, placeholder = { Text("Por ejemplo, Películas y series") }, isError = text.trim().length > NoteRules.TOPIC_MAX_LENGTH, supportingText = { Text("${text.trim().length}/${NoteRules.TOPIC_MAX_LENGTH}") }, modifier = Modifier.testTag("topicName")) },
        confirmButton = { TextButton(enabled = !busy && text.isNotBlank() && text.trim().length <= NoteRules.TOPIC_MAX_LENGTH, onClick = { busy = true; scope.launch { try { onSave(text) } finally { busy = false } } }) { Text("Guardar") } },
        dismissButton = { TextButton(enabled = !busy, onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun EditNoteDialog(note: Note, onDismiss: () -> Unit, onSave: suspend (String) -> Boolean) {
    var text by rememberSaveable(note.id) { mutableStateOf(note.body) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() }, title = { Text("Editar nota") },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, enabled = !busy, modifier = Modifier.heightIn(max = 320.dp).testTag("editNoteInput"), label = { Text("Texto") }, isError = text.trim().length > NoteRules.NOTE_MAX_LENGTH) },
        confirmButton = { TextButton(enabled = !busy && text.isNotBlank() && text.trim().length <= NoteRules.NOTE_MAX_LENGTH, onClick = { busy = true; scope.launch { try { onSave(text) } finally { busy = false } } }) { Text("Guardar cambios") } },
        dismissButton = { TextButton(enabled = !busy, onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun EmptyPanel(icon: ImageVector, title: String, description: String, action: String? = null, onAction: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(icon, null, Modifier.padding(26.dp).size(40.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(24.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (action != null) {
            Spacer(Modifier.height(24.dp))
            Button(onClick = onAction, shape = RoundedCornerShape(16.dp)) { Icon(Icons.Outlined.Add, null); Spacer(Modifier.width(8.dp)); Text(action) }
        }
    }
}
