package com.example.project_mvvm.data.mapper

import com.example.project_mvvm.data.local.entity.CharacterEntity
import com.example.project_mvvm.data.local.entity.FavoriteEntity
import com.example.project_mvvm.data.remote.dto.CharacterDto
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterStatus
import com.example.project_mvvm.domain.model.CharacterSummary

/**
 * Traductor entre los formatos de la capa `data` (DTOs de la API, Entities de Room) y los
 * modelos de `domain`.
 *
 * Vive en `data` porque es lo único que conoce las dos cosas a la vez: `data` puede conocer a
 * `domain`, pero `domain` nunca a `data`. Es un `object` porque no guarda estado: se usa
 * directo, `CharacterMapper.toSummary(dto)`.
 *
 * Caminos:
 * ```
 * CharacterDto ──► CharacterSummary / CharacterDetail      (online)
 * CharacterDto ──► CharacterEntity  (guardar en la caché)
 * CharacterEntity ──► CharacterSummary / CharacterDetail   (offline)
 * CharacterSummary ⇄ FavoriteEntity                         (favoritos)
 * ```
 */
object CharacterMapper {

    // ---------- Estado ----------

    /**
     * Texto de la API → enum. Compara en minúsculas y manda cualquier valor desconocido a
     * [CharacterStatus.UNKNOWN], así un valor nuevo de la API no rompe la app.
     */
    fun toStatus(status: String): CharacterStatus = when (status.lowercase()) {
        "alive" -> CharacterStatus.ALIVE
        "dead" -> CharacterStatus.DEAD
        else -> CharacterStatus.UNKNOWN
    }

    /** Texto guardado en Room (nombre del enum) → enum, con el mismo resguardo. */
    private fun statusFromDb(value: String): CharacterStatus =
        CharacterStatus.entries.firstOrNull { it.name == value } ?: CharacterStatus.UNKNOWN

    // ---------- API → domain ----------

    fun toSummary(dto: CharacterDto): CharacterSummary = CharacterSummary(
        id = dto.id,
        name = dto.name,
        status = toStatus(dto.status),
        species = dto.species,
        imageUrl = dto.image
    )

    fun toDetail(dto: CharacterDto): CharacterDetail = CharacterDetail(
        id = dto.id,
        name = dto.name,
        status = toStatus(dto.status),
        species = dto.species,
        gender = dto.gender,
        origin = dto.origin.name,          // del objeto { name, url } solo nos quedamos con el nombre
        location = dto.location.name,
        imageUrl = dto.image,
        episodeCount = dto.episode.size    // la API manda una URL por episodio: las contamos
    )

    // ---------- API → Room (caché) ----------

    fun toEntity(dto: CharacterDto, page: Int): CharacterEntity = CharacterEntity(
        id = dto.id,
        page = page,
        name = dto.name,
        status = toStatus(dto.status).name,
        species = dto.species,
        gender = dto.gender,
        origin = dto.origin.name,
        location = dto.location.name,
        imageUrl = dto.image,
        episodeCount = dto.episode.size
    )

    // ---------- Room (caché) → domain ----------

    fun toSummary(entity: CharacterEntity): CharacterSummary = CharacterSummary(
        id = entity.id,
        name = entity.name,
        status = statusFromDb(entity.status),
        species = entity.species,
        imageUrl = entity.imageUrl
    )

    fun toDetail(entity: CharacterEntity): CharacterDetail = CharacterDetail(
        id = entity.id,
        name = entity.name,
        status = statusFromDb(entity.status),
        species = entity.species,
        gender = entity.gender,
        origin = entity.origin,
        location = entity.location,
        imageUrl = entity.imageUrl,
        episodeCount = entity.episodeCount
    )

    // ---------- Favoritos ----------

    fun toFavoriteEntity(character: CharacterSummary, addedAt: Long): FavoriteEntity = FavoriteEntity(
        id = character.id,
        name = character.name,
        status = character.status.name,
        species = character.species,
        imageUrl = character.imageUrl,
        addedAt = addedAt
    )

    fun toSummary(favorite: FavoriteEntity): CharacterSummary = CharacterSummary(
        id = favorite.id,
        name = favorite.name,
        status = statusFromDb(favorite.status),
        species = favorite.species,
        imageUrl = favorite.imageUrl
    )
}
