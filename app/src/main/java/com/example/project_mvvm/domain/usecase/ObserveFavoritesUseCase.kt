package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso: observar la lista de favoritos.
 *
 * Devuelve un [Flow] que emite una lista nueva cada vez que se agrega o se quita un
 * favorito, así la pantalla de favoritos se actualiza sola.
 */
class ObserveFavoritesUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(): Flow<List<CharacterSummary>> = repository.observeFavorites()
}
