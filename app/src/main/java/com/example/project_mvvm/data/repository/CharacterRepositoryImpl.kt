package com.example.project_mvvm.data.repository

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.data.mapper.CharacterMapper
import com.example.project_mvvm.data.remote.RickAndMortyApi
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
) : CharacterRepository {

    override suspend fun getCharacters(page: Int): Resource<List<CharacterSummary>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCharacters(page)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Resource.Success(body.results.map { CharacterMapper.toSummary(it) })
                } else {
                    Resource.Error("Error del servidor (código ${response.code()})")
                }
            } catch (e: IOException) {
                Resource.Error("Sin conexión. Revisá tu internet.")
            } catch (e: Exception) {
                Resource.Error("Error inesperado: ${e.localizedMessage}")
            }
        }

    override suspend fun getCharacterDetail(id: Int): Resource<CharacterDetail> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCharacter(id)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Resource.Success(CharacterMapper.toDetail(body))
                } else if (response.code() == 404) {
                    Resource.Error("No existe un personaje con id $id")
                } else {
                    Resource.Error("Error del servidor (código ${response.code()})")
                }
            } catch (e: IOException) {
                Resource.Error("Sin conexión. Revisá tu internet.")
            } catch (e: Exception) {
                Resource.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
}
