package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(page: Int = 1): Resource<List<CharacterSummary>> {
        if (page < 1) return Resource.Error("La página tiene que ser 1 o mayor")
        return repository.getCharacters(page)
    }
}