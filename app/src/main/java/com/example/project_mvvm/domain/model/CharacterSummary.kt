package com.example.project_mvvm.domain.model

/**
 * Un personaje tal como lo necesitan la lista y la pantalla de favoritos.
 *
 * Es un modelo de dominio: Kotlin puro, sin nada de Android, Retrofit ni Room.
 * No se llama `Character` para no chocar con `java.lang.Character`, que Kotlin importa solo.
 *
 * @property id identificador del personaje en la API (empieza en 1).
 * @property imageUrl dirección de la foto; la carga Coil en la UI.
 */
data class CharacterSummary(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val imageUrl: String
)
