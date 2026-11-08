package com.example.project_mvvm.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.project_mvvm.core.model.UiState
import com.example.project_mvvm.core.theme.MortyDexTheme
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterStatus
import com.example.project_mvvm.presentation.common.components.ErrorView
import com.example.project_mvvm.presentation.common.components.LoadingView
import com.example.project_mvvm.presentation.common.components.OfflineBanner
import com.example.project_mvvm.presentation.common.components.StatusLine

/**
 * Pantalla de detalle (con estado): observa el ViewModel y delega el dibujo en
 * [CharacterDetailContent].
 *
 * @param onBackClick volver a la pantalla anterior.
 */
@Composable
fun CharacterDetailScreen(
    onBackClick: () -> Unit,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()

    CharacterDetailContent(
        state = state,
        isFavorite = isFavorite,
        isOffline = isOffline,
        onBackClick = onBackClick,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::load
    )
}

/** Pantalla de detalle (sin estado): barra con volver y ❤️, y el contenido según el estado. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailContent(
    state: UiState<CharacterDetail>,
    isFavorite: Boolean,
    isOffline: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text((state as? UiState.Success)?.data?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // El corazón solo aparece cuando hay un personaje cargado.
                    if (state is UiState.Success) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).fillMaxSize()) {
            if (isOffline) OfflineBanner()
            when (state) {
                UiState.Loading -> LoadingView()
                is UiState.Error -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { ErrorView(state.message, onRetry = onRetry) }
                is UiState.Success -> DetailBody(state.data)
            }
        }
    }
}

/** Foto grande, nombre, estado y una tarjeta con los datos. Hace scroll si no entra. */
@Composable
private fun DetailBody(detail: CharacterDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = detail.imageUrl,
            contentDescription = detail.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(Modifier.size(16.dp))
        Text(detail.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(4.dp))
        StatusLine(detail.status, detail.species)
        Spacer(Modifier.size(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                InfoRow("Género", detail.gender)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                InfoRow("Origen", detail.origin)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                InfoRow("Última ubicación", detail.location)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                InfoRow("Episodios", if (detail.episodeCount == 1) "1 episodio" else "${detail.episodeCount} episodios")
            }
        }
    }
}

/** Una fila "etiqueta: valor" de la tarjeta de datos. */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.size(16.dp))
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterDetailPreview() {
    MortyDexTheme {
        CharacterDetailContent(
            state = UiState.Success(
                CharacterDetail(
                    id = 1, name = "Rick Sanchez", status = CharacterStatus.ALIVE, species = "Human",
                    gender = "Male", origin = "Earth (C-137)", location = "Citadel of Ricks",
                    imageUrl = "", episodeCount = 51
                )
            ),
            isFavorite = true,
            isOffline = false,
            onBackClick = {},
            onToggleFavorite = {},
            onRetry = {}
        )
    }
}
