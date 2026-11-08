package com.example.project_mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.project_mvvm.data.local.entity.CharacterEntity

/**
 * Consultas a la tabla `characters` (la caché).
 *
 * DAO = Data Access Object. Se escribe una interfaz con anotaciones y Room genera (con KSP)
 * la clase que ejecuta el SQL. Las funciones `suspend` corren fuera del hilo principal,
 * así no congelan la pantalla.
 */
@Dao
interface CharacterDao {

    /**
     * Inserta los personajes, o los actualiza si ya existían (mismo id).
     * "Upsert" = update + insert. Así la caché siempre tiene la versión más nueva.
     */
    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    /** Los personajes guardados de una página, en el mismo orden que los manda la API. */
    @Query("SELECT * FROM characters WHERE page = :page ORDER BY id")
    suspend fun getByPage(page: Int): List<CharacterEntity>

    /** Un personaje por id, o `null` si nunca se descargó. */
    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getById(id: Int): CharacterEntity?
}
