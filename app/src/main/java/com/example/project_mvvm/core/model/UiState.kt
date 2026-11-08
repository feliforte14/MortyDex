package com.example.project_mvvm.core.model

/**
 * Estado de una pantalla que carga un dato una sola vez (por ejemplo, el detalle).
 *
 * Mientras [Resource] describe el resultado de la capa de datos, [UiState] describe
 * **qué tiene que dibujar la pantalla** en cada momento:
 * - [Loading]: un indicador de carga.
 * - [Success]: el contenido.
 * - [Error]: el mensaje y un botón para reintentar.
 *
 * Igual que en la Pokedex (`core/model/UiState.kt`).
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
