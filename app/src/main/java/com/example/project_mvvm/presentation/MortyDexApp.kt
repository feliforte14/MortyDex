package com.example.project_mvvm.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_mvvm.core.navigation.Screen
import com.example.project_mvvm.presentation.characters.CharacterListScreen
import com.example.project_mvvm.presentation.detail.CharacterDetailScreen
import com.example.project_mvvm.presentation.favorites.FavoritesScreen

/**
 * Raíz de la UI: el mapa de navegación entre pantallas.
 *
 * - `NavController`: recuerda la pila de pantallas (qué se abrió y en qué orden). `navigate`
 *   apila una pantalla y `popBackStack` vuelve a la anterior.
 * - `NavHost`: según la ruta actual, dibuja la pantalla correspondiente.
 *
 * Las pantallas no conocen al `NavController`: reciben lambdas (`onCharacterClick`,
 * `onBackClick`). Así una pantalla no sabe a dónde lleva cada acción, y es fácil de reutilizar.
 *
 * ```
 * characters ──tocar tarjeta──► character/{id}
 *     │                              ▲
 *     └──❤️──► favorites ──tocar tarjeta─┘
 * ```
 */
@Composable
fun MortyDexApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.CharacterList.route) {

        composable(Screen.CharacterList.route) {
            CharacterListScreen(
                onCharacterClick = { id -> navController.navigate(Screen.CharacterDetail.createRoute(id)) },
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) }
            )
        }

        composable(
            route = Screen.CharacterDetail.route,
            // El id viaja como Int; el DetailViewModel lo lee de SavedStateHandle con este nombre.
            arguments = listOf(navArgument(Screen.ARG_CHARACTER_ID) { type = NavType.IntType })
        ) {
            CharacterDetailScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onCharacterClick = { id -> navController.navigate(Screen.CharacterDetail.createRoute(id)) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
