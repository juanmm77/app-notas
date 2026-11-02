package com.notas.personal

import android.app.Application
import com.notas.personal.data.NotesDatabase
import com.notas.personal.data.NotesRepository

class NotesApplication : Application() {
    private val database by lazy { NotesDatabase.open(this) }
    val repository by lazy { NotesRepository(database) }
}
