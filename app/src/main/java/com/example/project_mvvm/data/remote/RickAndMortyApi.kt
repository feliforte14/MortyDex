package com.example.project_mvvm.data.remote

import com.example.project_mvvm.data.remote.dto.CharacterDto
import com.example.project_mvvm.data.remote.dto.CharacterListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Las direcciones de la API de Rick and Morty que usa la app.
 *
 * Es una interfaz sin código: Retrofit genera la implementación al ejecutar la app
 * (`retrofit.create(...)` en `di/NetworkModule`). La dirección base
 * (`https://rickandmortyapi.com/api/`) también se configura ahí.
 *
 * Se devuelve `Response<...>` (y no el DTO directo) para poder revisar el código HTTP
 * (200 OK, 404 no existe, 500 error del servidor) antes de usar el cuerpo.
 */
interface RickAndMortyApi {

    /** `GET character?page=N`: una página de 20 personajes. */
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int
    ): Response<CharacterListResponseDto>

    /** `GET character/{id}`: un personaje. Si el id no existe, responde 404. */
    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): Response<CharacterDto>
}
