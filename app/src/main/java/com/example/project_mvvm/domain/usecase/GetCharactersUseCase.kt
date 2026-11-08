package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterPage
import com.example.project_mvvm.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * Caso de uso: traer una página de la lista de personajes.
 *
 * Aplica la regla de negocio (la página tiene que ser 1 o mayor) antes de gastar una
 * llamada a internet. `@Inject constructor` le indica a Hilt cómo construirlo.
 */
class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    /** `operator fun invoke` permite llamarlo como una función: `getCharacters(2)`. */
    suspend operator fun invoke(page: Int = 1): Resource<CharacterPage> {
        if (page < 1) return Resource.Error("La página tiene que ser 1 o mayor")
        return repository.getCharacters(page)
    }
}
