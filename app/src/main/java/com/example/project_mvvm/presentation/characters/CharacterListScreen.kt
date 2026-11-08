package com.example.project_mvvm.presentation.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_mvvm.core.theme.MortyDexTheme
import com.example.project_mvvm.domain.model.CharacterStatus
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.presentation.common.components.CharacterCard
import com.example.project_mvvm.presentation.common.components.ErrorView
import com.example.project_mvvm.presentation.common.components.LoadingView
import com.example.project_mvvm.presentation.common.components.OfflineBanner

/**
 * Pantalla de lista (versión "con estado"): obtiene el ViewModel de Hilt, observa su estado
 * y se lo pasa a [CharacterListContent].
 *
 * `collectAsStateWithLifecycle` convierte el `StateFlow` en estado de Compose y deja de
 * escuchar cuando la app pasa a segundo plano (ahorra batería).
 *
 * @param onCharacterClick navegar al detalle con el id tocado.
 * @param onFavoritesClick navegar a la pantalla de favoritos.
 */
@Composable
fun CharacterListScreen(
    onCharacterClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CharacterListContent(
        state = state,
        onCharacterClick = onCharacterClick,
        onFavoritesClick = onFavoritesClick,
        onLoadMore = viewModel::loadNextPage
    )
}

/**
 * Pantalla de lista (versión "sin estado"): solo dibuja lo que recibe. Separarla del
 * ViewModel permite previsualizarla con @Preview y probarla con datos inventados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListContent(
    state: CharacterListUiState,
    onCharacterClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onLoadMore: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MortyDex") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favoritos")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).fillMaxSize()) {
            if (state.isOffline) OfflineBanner()

            when {
                // Primera carga: spinner en toda la pantalla.
                state.isLoading -> LoadingView()

                // Falló la primera página: error en toda la pantalla.
                state.characters.isEmpty() && state.errorMessage != null -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorView(message = state.errorMessage, onRetry = onLoadMore)
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // key: le dice a Compose qué tarjeta es cuál, así no redibuja todas al agregar más.
                    items(state.characters, key = { it.id }) { character ->
                        CharacterCard(character = character, onClick = { onCharacterClick(character.id) })
                    }
                    item { ListFooter(state, onLoadMore) }
                }
            }
        }
    }
}

/**
 * Último elemento de la lista. LazyColumn solo compone lo visible, así que cuando este pie
 * aparece en pantalla es porque el usuario llegó al final: ahí pedimos la página siguiente
 * (scroll infinito).
 */
@Composable
private fun ListFooter(state: CharacterListUiState, onLoadMore: () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        when {
            state.errorMessage != null -> ErrorView(state.errorMessage, onRetry = onLoadMore)
            state.isLoadingMore -> CircularProgressIndicator()
            state.canLoadMore -> {
                // Se ejecuta una vez por cada tamaño de lista: al llegar al final de 20, de 40...
                LaunchedEffect(state.characters.size) { onLoadMore() }
                CircularProgressIndicator()
            }
            else -> Text("No hay más personajes")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterListPreview() {
    MortyDexTheme {
        CharacterListContent(
            state = CharacterListUiState(
                characters = listOf(
                    CharacterSummary(1, "Rick Sanchez", CharacterStatus.ALIVE, "Human", ""),
                    CharacterSummary(2, "Morty Smith", CharacterStatus.ALIVE, "Human", ""),
                    CharacterSummary(8, "Adjudicator Rick", CharacterStatus.DEAD, "Human", "")
                ),
                canLoadMore = false,
                isOffline = true
            ),
            onCharacterClick = {},
            onFavoritesClick = {},
            onLoadMore = {}
        )
    }
}
