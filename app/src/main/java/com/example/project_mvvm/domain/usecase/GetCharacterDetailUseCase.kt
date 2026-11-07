package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Resource<CharacterDetail>
    {
        if (id < 1) return Resource.Error("ID de personaje Invalido")
        return repository.getCharacterDetail(id)
    }
}