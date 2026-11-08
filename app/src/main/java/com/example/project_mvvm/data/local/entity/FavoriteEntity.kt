package com.example.project_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla `favorites`: los personajes que el usuario marcó con ❤️.
 *
 * Es una tabla separada de la caché porque tienen vidas distintas: la caché se puede
 * reemplazar en cualquier momento con datos nuevos de internet, pero los favoritos son una
 * decisión del usuario y no se tienen que perder.
 *
 * Guarda los datos necesarios para mostrar la tarjeta, así la pantalla de favoritos funciona
 * sin internet.
 *
 * @property id mismo id que en la API; como es la clave primaria, un personaje no puede estar
 * dos veces en favoritos.
 * @property addedAt momento en que se agregó (milisegundos); sirve para ordenar del más nuevo
 * al más viejo.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val imageUrl: String,
    val addedAt: Long
)
