package com.example.project_mvvm.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.project_mvvm.core.theme.MortyDexTheme
import com.example.project_mvvm.core.theme.StatusAlive
import com.example.project_mvvm.core.theme.StatusDead
import com.example.project_mvvm.core.theme.StatusUnknown
import com.example.project_mvvm.domain.model.CharacterStatus
import com.example.project_mvvm.domain.model.CharacterSummary

/*
 * Componentes reutilizables por varias pantallas (lista, favoritos, detalle).
 * Son "tontos" (stateless): reciben datos y avisan eventos por parámetros, no conocen
 * ViewModels. Eso los hace fáciles de reutilizar y de previsualizar con @Preview.
 */

/** Indicador de carga centrado en toda la pantalla. */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/**
 * Mensaje de error con botón para reintentar.
 *
 * @param onRetry qué hacer al tocar "Reintentar" (normalmente, volver a pedirle los datos al ViewModel).
 */
@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.size(16.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

/** Mensaje centrado para listas vacías (por ejemplo, "Todavía no tenés favoritos"). */
@Composable
fun EmptyView(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Franja que avisa que se están mostrando datos guardados porque no hay internet. */
@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.tertiaryContainer, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.CloudOff, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Sin conexión: mostrando datos guardados", style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Color que representa cada estado: verde vivo, rojo muerto, gris desconocido. */
fun CharacterStatus.color(): Color = when (this) {
    CharacterStatus.ALIVE -> StatusAlive
    CharacterStatus.DEAD -> StatusDead
    CharacterStatus.UNKNOWN -> StatusUnknown
}

/** Texto en español de cada estado. */
fun CharacterStatus.label(): String = when (this) {
    CharacterStatus.ALIVE -> "Vivo"
    CharacterStatus.DEAD -> "Muerto"
    CharacterStatus.UNKNOWN -> "Desconocido"
}

/** Punto de color + estado + especie, por ejemplo "● Vivo · Human". */
@Composable
fun StatusLine(status: CharacterStatus, species: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(status.color())
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "${status.label()} · $species",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Tarjeta de un personaje en la lista y en favoritos: foto, nombre, estado y especie.
 *
 * `AsyncImage` (Coil) descarga la foto en segundo plano y la guarda en su propia caché,
 * así que al volver a la lista no se baja de nuevo.
 */
@Composable
fun CharacterCard(
    character: CharacterSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(4.dp))
                StatusLine(character.status, character.species)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterCardPreview() {
    MortyDexTheme {
        CharacterCard(
            character = CharacterSummary(1, "Rick Sanchez", CharacterStatus.ALIVE, "Human", ""),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    MortyDexTheme { ErrorView("Sin conexión. Revisá tu internet.", onRetry = {}) }
}
