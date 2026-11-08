package com.example.project_mvvm.domain.repository

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterPage
import com.example.project_mvvm.domain.model.CharacterSummary
import kotlinx.coroutines.flow.Flow

/**
 * Contrato ("menú") de los datos de personajes.
 *
 * `domain` dice **qué** se puede pedir; la capa `data` (`CharacterRepositoryImpl`) decide
 * **cómo**: internet con Retrofit, caché y favoritos con Room. Es la inversión de
 * dependencias (la D de SOLID): el ViewModel depende de esta interfaz, nunca de Retrofit o
 * Room, y en los tests se reemplaza por un repositorio falso.
 */
interface CharacterRepository {

    /**
     * Trae una página de personajes. Si no hay internet, devuelve la página guardada
     * en la caché (con `fromCache = true`) o un error si nunca se descargó.
     */
    suspend fun getCharacters(page: Int): Resource<CharacterPage>

    /** Trae el detalle de un personaje, con la misma estrategia de caché que la lista. */
    suspend fun getCharacterDetail(id: Int): Resource<CharacterDetail>

    /**
     * Favoritos guardados, del más reciente al más viejo.
     *
     * Es un [Flow]: emite una lista nueva cada vez que se agrega o se quita un favorito,
     * así la pantalla se actualiza sola.
     */
    fun observeFavorites(): Flow<List<CharacterSummary>>

    /** Emite `true` o `false` según el personaje esté en favoritos, y vuelve a emitir si cambia. */
    fun observeIsFavorite(id: Int): Flow<Boolean>

    /** Si el personaje es favorito lo quita; si no, lo agrega. */
    suspend fun toggleFavorite(character: CharacterSummary)
}
