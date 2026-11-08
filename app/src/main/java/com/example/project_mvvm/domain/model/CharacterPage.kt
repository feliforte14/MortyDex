package com.example.project_mvvm.domain.model

/**
 * Una página de la lista de personajes.
 *
 * La API devuelve los personajes de a 20. Además de la lista, la pantalla necesita saber
 * si hay más páginas para seguir cargando cuando el usuario llega al final.
 *
 * @property page número de esta página (empieza en 1).
 * @property hasNextPage `true` si existe una página siguiente.
 */
data class CharacterPage(
    val characters: List<CharacterSummary>,
    val page: Int,
    val hasNextPage: Boolean
)
