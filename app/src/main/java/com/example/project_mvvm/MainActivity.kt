package com.example.project_mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.project_mvvm.core.theme.MortyDexTheme
import com.example.project_mvvm.presentation.MortyDexApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la app. Solo aplica el tema y muestra [MortyDexApp], que contiene la
 * navegación entre pantallas.
 *
 * `@AndroidEntryPoint` permite que las pantallas que viven dentro de esta Activity pidan
 * ViewModels a Hilt con `hiltViewModel()`.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MortyDexTheme {
                MortyDexApp()
            }
        }
    }
}
