package com.example.project_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla `characters`: la **caché** de personajes descargados.
 *
 * Cada vez que se baja una página de internet, sus personajes se guardan acá. Si después no
 * hay conexión, la lista y el detalle se leen de esta tabla.
 *
 * Como la respuesta de la lista ya trae todos los datos del personaje, la misma fila sirve
 * para la lista y para el detalle.
 *
 * @property page página de la API en la que vino el personaje; permite reconstruir la lista
 * offline en el mismo orden. Tiene índice porque se consulta por página.
 * @property status nombre del enum (`ALIVE`, `DEAD`, `UNKNOWN`). Room guarda texto; el mapper
 * lo vuelve a convertir.
 */
@Entity(
    tableName = "characters",
    indices = [Index(value = ["page"])]
)
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val page: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodeCount: Int
)
