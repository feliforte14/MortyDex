package com.example.project_mvvm.core.navigation

/**
 * Rutas de navegación de la app.
 *
 * Cada pantalla tiene una ruta (un texto, parecido a una dirección web). Las rutas con
 * `{argumento}` reciben datos: `character/{characterId}` se navega como `character/1`.
 * Tener las rutas en un solo lugar evita escribirlas a mano (y mal) en varios archivos.
 */
sealed class Screen(val route: String) {

    /** Lista paginada de personajes. Es la pantalla de inicio. */
    data object CharacterList : Screen("characters")

    /** Detalle de un personaje. Recibe su id. */
    data object CharacterDetail : Screen("character/{$ARG_CHARACTER_ID}") {
        /** Arma la ruta concreta, por ejemplo `character/1`. */
        fun createRoute(characterId: Int): String = "character/$characterId"
    }

    /** Personajes marcados como favoritos (guardados en Room). */
    data object Favorites : Screen("favorites")

    companion object {
        /** Nombre del argumento con el id; lo usan la navegación y el DetailViewModel. */
        const val ARG_CHARACTER_ID = "characterId"
    }
}
