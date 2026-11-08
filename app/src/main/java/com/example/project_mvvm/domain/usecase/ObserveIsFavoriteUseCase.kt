package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso: observar si un personaje está en favoritos.
 *
 * Lo usa el detalle para pintar el corazón lleno o vacío. Si el usuario lo toca,
 * el [Flow] emite el valor nuevo y el ícono cambia sin recargar nada.
 */
class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(id: Int): Flow<Boolean> = repository.observeIsFavorite(id)
}
