package com.example.project_mvvm.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Datos de paginación que vienen en `info`.
 *
 * `next` y `prev` llevan `?` porque pueden venir `null`: en la página 1 no hay anterior y en
 * la última no hay siguiente. Si se declararan sin `?`, Moshi fallaría al leer esas páginas.
 */
@JsonClass(generateAdapter = true)
data class PageInfoDto(
    @Json(name = "count") val count: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "next") val next: String?,
    @Json(name = "prev") val prev: String?
)

/** Respuesta completa de `GET character?page=N`: paginación + los 20 personajes. */
@JsonClass(generateAdapter = true)
data class CharacterListResponseDto(
    @Json(name = "info") val info: PageInfoDto,
    @Json(name = "results") val results: List<CharacterDto>
)
