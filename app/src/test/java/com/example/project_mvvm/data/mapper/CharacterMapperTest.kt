package com.example.project_mvvm.data.mapper

import com.example.project_mvvm.data.remote.dto.CharacterDto
import com.example.project_mvvm.data.remote.dto.LocationDto
import com.example.project_mvvm.domain.model.CharacterStatus
import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests del traductor DTO / Entity ⇄ domain. No necesitan Android: corren en la JVM. */
class CharacterMapperTest {

    private val rickDto = CharacterDto(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        gender = "Male",
        origin = LocationDto("Earth (C-137)", "https://rickandmortyapi.com/api/location/1"),
        location = LocationDto("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = List(51) { "https://rickandmortyapi.com/api/episode/${it + 1}" }
    )

    @Test
    fun `toStatus convierte los textos de la API sin importar mayusculas`() {
        assertEquals(CharacterStatus.ALIVE, CharacterMapper.toStatus("Alive"))
        assertEquals(CharacterStatus.DEAD, CharacterMapper.toStatus("dead"))
        assertEquals(CharacterStatus.UNKNOWN, CharacterMapper.toStatus("unknown"))
    }

    @Test
    fun `toStatus manda un valor desconocido a UNKNOWN`() {
        assertEquals(CharacterStatus.UNKNOWN, CharacterMapper.toStatus("Missing"))
    }

    @Test
    fun `toDetail toma el nombre del origen y cuenta los episodios`() {
        val detail = CharacterMapper.toDetail(rickDto)
        assertEquals("Earth (C-137)", detail.origin)
        assertEquals("Citadel of Ricks", detail.location)
        assertEquals(51, detail.episodeCount)
        assertEquals(rickDto.image, detail.imageUrl)
    }

    @Test
    fun `guardar en cache y volver a leer da el mismo detalle`() {
        val entity = CharacterMapper.toEntity(rickDto, page = 1)
        assertEquals(1, entity.page)
        assertEquals("ALIVE", entity.status)
        assertEquals(CharacterMapper.toDetail(rickDto), CharacterMapper.toDetail(entity))
    }

    @Test
    fun `favorito ida y vuelta conserva los datos`() {
        val summary = CharacterMapper.toSummary(rickDto)
        val favorite = CharacterMapper.toFavoriteEntity(summary, addedAt = 123L)
        assertEquals(123L, favorite.addedAt)
        assertEquals(summary, CharacterMapper.toSummary(favorite))
    }
}
