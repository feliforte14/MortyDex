package com.example.project_mvvm.domain.repository

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterSummary

interface CharacterRepository {
    suspend fun getCharacters(page: Int): Resource<List<CharacterSummary>>
    suspend fun getCharacterDetail(id: Int): Resource<CharacterDetail>
}