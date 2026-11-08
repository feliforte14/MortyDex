package com.example.project_mvvm.data.repository

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.data.local.dao.CharacterDao
import com.example.project_mvvm.data.local.dao.FavoriteDao
import com.example.project_mvvm.data.mapper.CharacterMapper
import com.example.project_mvvm.data.remote.RickAndMortyApi
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.CharacterPage
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.repository.CharacterRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del "menú" [CharacterRepository]: decide de dónde salen los datos.
 *
 * Estrategia para lista y detalle (**primero la red, la caché como respaldo**):
 * ```
 * api.getCharacters(page)
 *   ├─ OK         → guardar en Room → Resource.Success(datos)
 *   ├─ HTTP error → Resource.Error("Error del servidor (código N)")
 *   └─ sin red    → ¿está en Room? ─ sí → Resource.Success(datos, fromCache = true)
 *                                   └ no → Resource.Error("Sin conexión...")
 * ```
 * Los favoritos viven solo en Room.
 *
 * Regla del archivo: **nunca deja escapar una excepción** (salvo la cancelación de la
 * corrutina). Pase lo que pase devuelve un [Resource], así el ViewModel solo pregunta
 * Success o Error.
 *
 * `@Singleton`: hay una sola instancia en toda la app. `@Inject constructor`: Hilt le pasa
 * la API y los DAOs (ver `di/NetworkModule` y `di/DatabaseModule`).
 */
@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
    private val characterDao: CharacterDao,
    private val favoriteDao: FavoriteDao
) : CharacterRepository {

    override suspend fun getCharacters(page: Int): Resource<CharacterPage> =
        // Dispatchers.IO: hilo de fondo pensado para red y base de datos.
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCharacters(page)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    // Guardamos la página en la caché para poder mostrarla sin internet.
                    characterDao.upsertAll(body.results.map { CharacterMapper.toEntity(it, page) })
                    Resource.Success(
                        CharacterPage(
                            characters = body.results.map { CharacterMapper.toSummary(it) },
                            page = page,
                            hasNextPage = body.info.next != null
                        )
                    )
                } else {
                    Resource.Error(httpErrorMessage(response.code()))
                }
            } catch (e: IOException) {
                // IOException = no se pudo conectar (modo avión, sin señal, servidor caído).
                cachedPage(page)
                    ?: Resource.Error("Sin conexión. Revisá tu internet.")
            } catch (e: CancellationException) {
                throw e // la corrutina se canceló (por ejemplo, se cerró la pantalla): no es un error
            } catch (e: Exception) {
                Resource.Error("Error inesperado: ${e.localizedMessage}")
            }
        }

    override suspend fun getCharacterDetail(id: Int): Resource<CharacterDetail> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCharacter(id)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Resource.Success(CharacterMapper.toDetail(body))
                } else if (response.code() == 404) {
                    Resource.Error("No existe un personaje con id $id")
                } else {
                    Resource.Error(httpErrorMessage(response.code()))
                }
            } catch (e: IOException) {
                characterDao.getById(id)
                    ?.let { Resource.Success(CharacterMapper.toDetail(it), fromCache = true) }
                    ?: Resource.Error("Sin conexión. Revisá tu internet.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Resource.Error("Error inesperado: ${e.localizedMessage}")
            }
        }

    /**
     * Mensaje para una respuesta HTTP que no fue exitosa.
     * 429 = "Too Many Requests": la API limita cuántos pedidos se hacen por segundo
     * (pasa si se hace scroll muy rápido). Se resuelve esperando y tocando "Reintentar".
     */
    private fun httpErrorMessage(code: Int): String = when (code) {
        429 -> "Demasiados pedidos seguidos. Esperá un momento y reintentá."
        in 500..599 -> "El servidor no está disponible (código $code)"
        else -> "Error del servidor (código $code)"
    }

    /** Página guardada en Room, o `null` si nunca se descargó. */
    private suspend fun cachedPage(page: Int): Resource<CharacterPage>? {
        val cached = characterDao.getByPage(page)
        if (cached.isEmpty()) return null
        return Resource.Success(
            CharacterPage(
                characters = cached.map { CharacterMapper.toSummary(it) },
                page = page,
                // Sin internet no sabemos cuántas páginas hay: dejamos seguir. Si la siguiente
                // tampoco está guardada, se mostrará "Sin conexión" con "Reintentar".
                hasNextPage = true
            ),
            fromCache = true
        )
    }

    override fun observeFavorites(): Flow<List<CharacterSummary>> =
        favoriteDao.observeAll().map { list -> list.map { CharacterMapper.toSummary(it) } }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favoriteDao.observeIsFavorite(id)

    override suspend fun toggleFavorite(character: CharacterSummary) {
        if (favoriteDao.isFavorite(character.id)) {
            favoriteDao.deleteById(character.id)
        } else {
            favoriteDao.insert(CharacterMapper.toFavoriteEntity(character, System.currentTimeMillis()))
        }
    }
}
