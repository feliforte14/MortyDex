package com.example.project_mvvm.core.model

/**
 * Resultado de una operación de la capa de datos (lo que devuelve el repositorio).
 *
 * Es una "caja" con solo dos posibilidades:
 * - [Success]: salió bien y trae los datos.
 * - [Error]: salió mal y trae un mensaje listo para mostrar al usuario.
 *
 * Al ser `sealed`, un `when (resource)` obliga a contemplar los dos casos:
 * no hay forma de olvidarse del error.
 *
 * `out T` permite que un `Resource<List<CharacterSummary>>` se use donde se espera un
 * `Resource<List<Any>>`, y `Nothing` hace que un único [Error] sirva para cualquier `T`.
 */
sealed class Resource<out T> {

    /**
     * La operación salió bien.
     *
     * @property data los datos obtenidos.
     * @property fromCache `true` si los datos no vinieron de internet sino de la caché local
     * (Room), por ejemplo porque no había conexión. La UI lo usa para avisarle al usuario.
     */
    data class Success<out T>(val data: T, val fromCache: Boolean = false) : Resource<T>()

    /**
     * La operación falló.
     *
     * @property message texto amigable que explica qué pasó ("Sin conexión...", "No existe...").
     */
    data class Error(val message: String) : Resource<Nothing>()
}
