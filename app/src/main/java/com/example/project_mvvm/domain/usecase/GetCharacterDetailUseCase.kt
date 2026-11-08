package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * Caso de uso: traer el detalle de un personaje.
 *
 * Regla: en la API los ids empiezan en 1, así que un id menor es un error y no se consulta.
 */
class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Resource<CharacterDetail> {
        if (id < 1) return Resource.Error("ID de personaje inválido: $id")
        return repository.getCharacterDetail(id)
    }
}
