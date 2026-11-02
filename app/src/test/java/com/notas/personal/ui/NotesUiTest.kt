package com.notas.personal.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.notas.personal.NotesViewModel
import com.notas.personal.data.NotesDatabase
import com.notas.personal.data.NotesRepository
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NotesUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private lateinit var database: NotesDatabase
    private lateinit var repository: NotesRepository

    @Before fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), NotesDatabase::class.java).build()
        repository = NotesRepository(database)
    }
    @After fun tearDown() { database.close() }

    private fun awaitText(text: String) {
        compose.waitUntil(10_000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun screenshot(name: String) {
        compose.waitForIdle()
        val directory = File(System.getProperty("notes.screenshots") ?: "build/screenshots")
        directory.mkdirs()
        compose.runOnIdle {
            val view = compose.activity.findViewById<View>(android.R.id.content)
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            File(directory, "$name.png").outputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            bitmap.recycle()
        }
    }

    @Test fun createRenameSendEditAndConfirmDelete() {
        val model = NotesViewModel(repository, SavedStateHandle())
        compose.setContent { NotesTheme(dark = false) { NotesApp(model) } }
        awaitText("Crear mi primera temática")
        screenshot("01-inicio-vacio")
        compose.onNodeWithText("Crear mi primera temática").performClick()
        compose.onNodeWithTag("topicName").performTextInput("Películas y series")
        compose.onNodeWithText("Guardar").performClick()
        awaitText("Películas y series")
        compose.onNodeWithContentDescription("Guardar nota").assertIsNotEnabled()
        compose.onNodeWithTag("noteInput").performTextInput("Ver El viaje de Chihiro este fin de semana.")
        compose.onNodeWithContentDescription("Guardar nota").performClick()
        awaitText("1 nota")
        compose.onNodeWithTag("noteInput").performTextInput("Una historia para volver a ver ✨")
        compose.onNodeWithContentDescription("Guardar nota").performClick()
        awaitText("2 notas")
        screenshot("02-conversacion-clara")
        compose.onNodeWithContentDescription("Renombrar temática").performClick()
        compose.onNodeWithTag("topicName").performTextReplacement("Cine y series")
        compose.onNodeWithText("Guardar").performClick()
        awaitText("Cine y series")
        compose.onNodeWithText("Una historia para volver a ver ✨").performTouchInput { longClick() }
        compose.onNodeWithText("Editar texto").performClick()
        compose.onNodeWithTag("editNoteInput").performTextReplacement("Recomendarla después de verla.")
        compose.onNodeWithText("Guardar cambios").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("editNoteInput").fetchSemanticsNodes().isEmpty() }
        awaitText("Recomendarla después de verla.")
        compose.onNodeWithText("Recomendarla después de verla.").performTouchInput { longClick() }
        compose.onNodeWithText("Eliminar nota").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Recomendarla después de verla.").assertExists()
        compose.onNodeWithText("Recomendarla después de verla.").performTouchInput { longClick() }
        compose.onNodeWithText("Eliminar nota").performClick()
        compose.onNodeWithText("Eliminar", substring = false).performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Recomendarla después de verla.").fetchSemanticsNodes().isEmpty() }
        compose.onNodeWithContentDescription("Volver a temáticas").performClick()
        awaitText("Mis notas")
        compose.onNodeWithText("Cine y series").assertExists()
        compose.onNodeWithText("Ver El viaje de Chihiro este fin de semana.").assertExists()
    }

    @Test
    @Config(qualifiers = "w411dp-h891dp-xxhdpi")
    fun darkThemeAndPopulatedTopicList() {
        runBlocking {
            val cinema = repository.createTopic("Películas y series")
            repository.addNote(cinema, "Ver El viaje de Chihiro este fin de semana.")
            val gym = repository.createTopic("Rutina gym")
            repository.addNote(gym, "Lunes: piernas y 15 minutos de movilidad.")
            val ideas = repository.createTopic("Ideas y proyectos")
            repository.addNote(ideas, "Las ideas pequeñas también merecen un lugar.")
        }
        val model = NotesViewModel(repository, SavedStateHandle())
        compose.setContent { NotesTheme(dark = true) { NotesApp(model) } }
        awaitText("Películas y series")
        screenshot("03-tematicas-oscuras")
        compose.onNodeWithText("Películas y series").performClick()
        awaitText("Ver El viaje de Chihiro este fin de semana.")
        screenshot("04-conversacion-oscura")
    }
}
