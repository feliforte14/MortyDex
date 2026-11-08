package com.example.project_mvvm.domain.model

/**
 * Un personaje con toda la información que muestra la pantalla de detalle.
 *
 * No copia la forma del JSON: la API manda `origin` como objeto `{ name, url }` y los
 * episodios como lista de URLs, pero acá guardamos solo lo que la pantalla usa
 * (el nombre del origen y la cantidad de episodios).
 *
 * @property origin planeta o lugar de origen, por ejemplo "Earth (C-137)".
 * @property location última ubicación conocida.
 * @property episodeCount en cuántos episodios aparece.
 */
data class CharacterDetail(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodeCount: Int
)

/**
 * Versión resumida del detalle. Se usa para guardar el personaje en favoritos
 * desde la pantalla de detalle.
 */
fun CharacterDetail.toSummary(): CharacterSummary = CharacterSummary(
    id = id,
    name = name,
    status = status,
    species = species,
    imageUrl = imageUrl
)
