package com.example.project_mvvm.data.remote

import com.example.project_mvvm.data.remote.dto.CharacterDto
import com.example.project_mvvm.data.remote.dto.CharacterListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int
    ): Response<CharacterListResponseDto>

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): Response<CharacterDto>
}
