package com.example.project_mvvm.data.mapper

import com.example.project_mvvm.data.remote.dto.CharacterDto
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterStatus
import com.example.project_mvvm.domain.model.CharacterSummary

object CharacterMapper {

    fun toStatus(status: String): CharacterStatus = when (status.lowercase()) {
        "alive" -> CharacterStatus.ALIVE
        "dead" -> CharacterStatus.DEAD
        else -> CharacterStatus.UNKNOWN
    }

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
        origin = dto.origin.name,
        location = dto.location.name,
        imageUrl = dto.image,
        episodeCount = dto.episode.size
    )
}
