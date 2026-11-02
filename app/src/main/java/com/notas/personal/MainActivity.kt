package com.notas.personal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.notas.personal.ui.NotesApp
import com.notas.personal.ui.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val notesViewModel: NotesViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    NotesViewModel((application as NotesApplication).repository, createSavedStateHandle())
                }
            })
            NotesTheme { NotesApp(notesViewModel) }
        }
    }
}
