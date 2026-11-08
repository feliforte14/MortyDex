package com.example.project_mvvm.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.usecase.ObserveFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel de la pantalla de favoritos.
 *
 * No tiene funciones de "cargar": el Flow de Room emite la lista apenas se observa y vuelve a
 * emitir cada vez que se agrega o se quita un favorito (por ejemplo, desde el detalle).
 *
 * `null` significa "todavía no llegó la primera lectura de Room", para no mostrar
 * "no tenés favoritos" durante un instante antes de que lleguen.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavorites: ObserveFavoritesUseCase
) : ViewModel() {

    val favorites: StateFlow<List<CharacterSummary>?> = observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
