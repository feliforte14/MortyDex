package com.example.project_mvvm.core.theme

import androidx.compose.ui.graphics.Color

/*
 * Colores de la app. Los "80" se usan en modo oscuro y los "40" en modo claro
 * (ver Theme.kt). En Android 12+ el tema usa los colores del fondo de pantalla
 * (dynamic color) y estos quedan como respaldo.
 */

// Verde "portal", inspirado en la serie.
val PortalGreen80 = Color(0xFFA6E37A)
val PortalGreen40 = Color(0xFF3B7A1E)
val Teal80 = Color(0xFF8FD5D0)
val Teal40 = Color(0xFF00696A)
val Amber80 = Color(0xFFF2C46B)
val Amber40 = Color(0xFF7A5900)

// Colores del indicador de estado del personaje (se usan igual en claro y oscuro).
val StatusAlive = Color(0xFF43A047)
val StatusDead = Color(0xFFE53935)
val StatusUnknown = Color(0xFF9E9E9E)
