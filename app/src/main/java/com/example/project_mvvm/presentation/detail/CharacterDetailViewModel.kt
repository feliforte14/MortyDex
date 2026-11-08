package com.example.project_mvvm.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.core.model.UiState
import com.example.project_mvvm.core.navigation.Screen
import com.example.project_mvvm.domain.model.CharacterDetail
import com.example.project_mvvm.domain.model.toSummary
import com.example.project_mvvm.domain.usecase.GetCharacterDetailUseCase
import com.example.project_mvvm.domain.usecase.ObserveIsFavoriteUseCase
import com.example.project_mvvm.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel del detalle de un personaje.
 *
 * El id llega por la ruta de navegación (`character/{characterId}`). [SavedStateHandle] lo
 * trae solo (Hilt + Navigation lo completan) y además sobrevive a que Android mate el proceso.
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetail: GetCharacterDetailUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    observeIsFavorite: ObserveIsFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Int = checkNotNull(savedStateHandle[Screen.ARG_CHARACTER_ID]) {
        "Falta el argumento ${Screen.ARG_CHARACTER_ID} en la navegación"
    }

    private val _uiState = MutableStateFlow<UiState<CharacterDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<CharacterDetail>> = _uiState.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    /** `true` si el detalle se leyó de la caché porque no había internet. */
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    /**
     * Si el personaje está en favoritos. `stateIn` convierte el Flow de Room en un StateFlow:
     * empieza en `false` y se actualiza solo cada vez que cambia la tabla de favoritos.
     * `WhileSubscribed(5_000)` deja de escuchar a Room 5 segundos después de que la pantalla
     * deja de observarlo (y no se corta al rotar, que tarda menos).
     */
    val isFavorite: StateFlow<Boolean> = observeIsFavorite(characterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        load()
    }

    /** Carga (o recarga, desde "Reintentar") el detalle. */
    fun load() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = getCharacterDetail(characterId)) {
                is Resource.Success -> {
                    _isOffline.value = result.fromCache
                    UiState.Success(result.data)
                }
                is Resource.Error -> UiState.Error(result.message)
            }
        }
    }

    /** Agrega o quita de favoritos. Solo funciona cuando el detalle ya se cargó. */
    fun onToggleFavorite() {
        val detail = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch { toggleFavorite(detail.toSummary()) }
    }
}
