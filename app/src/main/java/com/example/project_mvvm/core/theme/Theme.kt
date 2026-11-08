package com.example.project_mvvm.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PortalGreen80,
    secondary = Teal80,
    tertiary = Amber80
)

private val LightColorScheme = lightColorScheme(
    primary = PortalGreen40,
    secondary = Teal40,
    tertiary = Amber40
)

/**
 * Tema Material 3 de MortyDex. Todas las pantallas se dibujan dentro de él (ver MainActivity),
 * así usan los mismos colores y tipografía vía `MaterialTheme.colorScheme` y
 * `MaterialTheme.typography`.
 *
 * @param darkTheme sigue la configuración del sistema por defecto.
 * @param dynamicColor en Android 12+ toma los colores del fondo de pantalla del usuario.
 */
@Composable
fun MortyDexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
