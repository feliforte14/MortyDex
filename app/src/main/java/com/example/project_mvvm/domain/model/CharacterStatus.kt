package com.example.project_mvvm.domain.model

/**
 * Estado vital de un personaje.
 *
 * La API lo manda como texto ("Alive", "Dead", "unknown"). Usar un enum en lugar de un
 * `String` hace que un error de tipeo ("alive" en minúscula) no compile, en vez de fallar
 * en silencio. La conversión texto → enum la hace el mapper de la capa `data`.
 */
enum class CharacterStatus {
    ALIVE,
    DEAD,
    UNKNOWN
}
