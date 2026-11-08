package com.example.project_mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project_mvvm.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Consultas a la tabla `favorites`.
 *
 * Las funciones que devuelven [Flow] no son `suspend`: Room las vuelve a ejecutar y emite
 * el resultado nuevo **cada vez que la tabla cambia**. Por eso la pantalla de favoritos y el
 * corazón del detalle se actualizan solos.
 */
@Dao
interface FavoriteDao {

    /** Todos los favoritos, del más reciente al más viejo. */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    /** Emite `true` mientras el personaje esté en favoritos. */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun observeIsFavorite(id: Int): Flow<Boolean>

    /** Consulta puntual (sin Flow), la usa el repositorio para decidir si agrega o quita. */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    /** Agrega un favorito. Si ya existía, lo reemplaza. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    /** Quita un favorito por id. */
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Int)
}
