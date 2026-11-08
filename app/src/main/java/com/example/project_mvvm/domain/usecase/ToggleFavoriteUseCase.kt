package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * Caso de uso: agregar o quitar un personaje de favoritos.
 *
 * Si ya era favorito lo quita; si no, lo agrega. Se guarda en Room, así que el cambio
 * sobrevive a cerrar la app.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(character: CharacterSummary) = repository.toggleFavorite(character)
}
