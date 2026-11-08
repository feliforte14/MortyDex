package com.example.project_mvvm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.project_mvvm.data.local.dao.CharacterDao
import com.example.project_mvvm.data.local.dao.FavoriteDao
import com.example.project_mvvm.data.local.entity.CharacterEntity
import com.example.project_mvvm.data.local.entity.FavoriteEntity

/**
 * La base de datos SQLite de la app, manejada por Room.
 *
 * - `entities`: las tablas que tiene.
 * - `version`: si en el futuro se cambia una tabla, se sube la versión y se escribe una
 *   `Migration` para no perder los datos del usuario (no se usa
 *   `fallbackToDestructiveMigration`, que borra todo).
 * - `exportSchema = true`: Room guarda el esquema de cada versión en `app/schemas/`. Esos
 *   archivos se suben a git y sirven para escribir y testear migraciones.
 *
 * La instancia la crea Hilt una sola vez, en `di/DatabaseModule`.
 */
@Database(
    entities = [CharacterEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MortyDexDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val NAME = "mortydex.db"
    }
}
