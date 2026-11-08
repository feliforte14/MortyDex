package com.example.project_mvvm.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 * DTO = Data Transfer Object: clases que copian la forma EXACTA del JSON de la API.
 * Moshi las llena a partir del texto JSON. No tienen lógica: solo copian.
 *
 * - @JsonClass(generateAdapter = true): Moshi genera (con KSP, al compilar) el código que
 *   lee la clase. Se ve en app/build/generated/ksp/.../CharacterDtoJsonAdapter.kt.
 * - @Json(name = "..."): nombre del campo en el JSON. Si un campo del JSON no está en la
 *   clase (type, url, created), Moshi lo ignora.
 */

/** Objeto `{ "name": ..., "url": ... }` que la API usa para `origin` y `location`. */
@JsonClass(generateAdapter = true)
data class LocationDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

/**
 * Un personaje tal como lo manda `GET character/{id}` (y cada elemento de `results`).
 *
 * `status` queda como texto ("Alive"); el mapper lo convierte a `CharacterStatus`.
 */
@JsonClass(generateAdapter = true)
data class CharacterDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "status") val status: String,
    @Json(name = "species") val species: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "origin") val origin: LocationDto,
    @Json(name = "location") val location: LocationDto,
    @Json(name = "image") val image: String,
    @Json(name = "episode") val episode: List<String>
)
