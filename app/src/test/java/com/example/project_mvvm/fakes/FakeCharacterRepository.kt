package com.example.project_mvvm.fakes

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterPage
import com.example.project_mvvm.domain.model.CharacterStatus
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repositorio falso para los tests unitarios.
 *
 * Como los casos de uso y ViewModels dependen de la **interfaz** [CharacterRepository], en
 * los tests les pasamos esta versión: no usa internet ni Room, responde al instante y nos deja
 * decidir qué devuelve. Es la ventaja práctica de la inversión de dependencias.
 */
class FakeCharacterRepository : CharacterRepository {

    /** Qué devolver para cada página. Si una página no está, se devuelve un error. */
    val pages = mutableMapOf<Int, Resource<CharacterPage>>()
    var detailResult: Resource<CharacterDetail> = Resource.Error("sin configurar")

    /** Páginas que se pidieron, en orden, para verificar que no se piden repetidas. */
    val requestedPages = mutableListOf<Int>()
    var detailRequests = 0

    private val favorites = MutableStateFlow<List<CharacterSummary>>(emptyList())

    override suspend fun getCharacters(page: Int): Resource<CharacterPage> {
        requestedPages += page
        return pages[page] ?: Resource.Error("Sin conexión. Revisá tu internet.")
    }

    override suspend fun getCharacterDetail(id: Int): Resource<CharacterDetail> {
        detailRequests++
        return detailResult
    }

    override fun observeFavorites(): Flow<List<CharacterSummary>> = favorites

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favorites.map { list -> list.any { it.id == id } }

    override suspend fun toggleFavorite(character: CharacterSummary) {
        favorites.value = if (favorites.value.any { it.id == character.id }) {
            favorites.value.filterNot { it.id == character.id }
        } else {
            favorites.value + character
        }
    }

    companion object {
        fun summary(id: Int) = CharacterSummary(id, "Personaje $id", CharacterStatus.ALIVE, "Human", "url$id")

        fun page(page: Int, ids: IntRange, hasNext: Boolean, fromCache: Boolean = false) =
            Resource.Success(CharacterPage(ids.map { summary(it) }, page, hasNext), fromCache)

        fun detail(id: Int) = CharacterDetail(
            id = id, name = "Personaje $id", status = CharacterStatus.DEAD, species = "Alien",
            gender = "Female", origin = "Earth", location = "Citadel", imageUrl = "url$id", episodeCount = 3
        )
    }
}
