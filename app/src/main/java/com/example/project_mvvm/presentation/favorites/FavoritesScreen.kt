package com.example.project_mvvm.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.presentation.common.components.CharacterCard
import com.example.project_mvvm.presentation.common.components.EmptyView
import com.example.project_mvvm.presentation.common.components.LoadingView

/**
 * Pantalla de favoritos (con estado). Los datos salen de Room, así que funciona sin internet
 * y sigue mostrando lo mismo después de cerrar y abrir la app.
 */
@Composable
fun FavoritesScreen(
    onCharacterClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    FavoritesContent(favorites, onCharacterClick, onBackClick)
}

/**
 * Pantalla de favoritos (sin estado).
 *
 * @param favorites `null` mientras Room hace la primera lectura.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    favorites: List<CharacterSummary>?,
    onCharacterClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
            favorites == null -> LoadingView(modifier)
            favorites.isEmpty() -> EmptyView(
                "Todavía no tenés favoritos.\nTocá el ❤️ en el detalle de un personaje para agregarlo.",
                modifier
            )
            else -> LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = { it.id }) { character ->
                    CharacterCard(character = character, onClick = { onCharacterClick(character.id) })
                }
            }
        }
    }
}
