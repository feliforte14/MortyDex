package com.example.project_mvvm.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.domain.model.CharacterSummary
import com.example.project_mvvm.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Todo lo que la pantalla de lista necesita para dibujarse.
 *
 * La lista es paginada, por eso no alcanza con `UiState` (cargando / éxito / error): puede
 * estar mostrando 40 personajes **y a la vez** cargando la página 3, o mostrando 40 y con un
 * error al pedir la siguiente. Una `data class` describe todas esas combinaciones.
 *
 * @property isLoading cargando la primera página (pantalla completa con spinner).
 * @property isLoadingMore cargando una página siguiente (spinner al final de la lista).
 * @property errorMessage último error; si no hay personajes ocupa la pantalla, si hay se
 * muestra al final con "Reintentar".
 * @property canLoadMore la API todavía tiene más páginas.
 * @property isOffline lo que se ve viene de la caché de Room.
 */
data class CharacterListUiState(
    val characters: List<CharacterSummary> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val canLoadMore: Boolean = true,
    val isOffline: Boolean = false
)

/**
 * ViewModel de la lista de personajes.
 *
 * - Sobrevive a la rotación de pantalla: la lista ya cargada no se vuelve a pedir.
 * - Expone un [StateFlow] de solo lectura; la pantalla lo observa y se redibuja con cada
 *   estado nuevo. Solo el ViewModel puede modificar `_uiState` (privado y mutable).
 * - `@HiltViewModel` + `@Inject constructor`: Hilt le pasa el caso de uso.
 */
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharacters: GetCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    /** Última página cargada con éxito (0 = ninguna todavía). */
    private var lastLoadedPage = 0

    init {
        loadNextPage()
    }

    /**
     * Pide la página siguiente. La pantalla la llama al llegar al final de la lista y al
     * tocar "Reintentar". Si ya hay una carga en curso o no hay más páginas, no hace nada:
     * así no se piden páginas repetidas aunque se llame varias veces.
     */
    fun loadNextPage() {
        val current = _uiState.value
        if (current.isLoading || current.isLoadingMore || !current.canLoadMore) return

        val isFirstPage = lastLoadedPage == 0
        _uiState.update {
            it.copy(isLoading = isFirstPage, isLoadingMore = !isFirstPage, errorMessage = null)
        }

        // viewModelScope: la corrutina se cancela sola si el ViewModel se destruye.
        viewModelScope.launch {
            when (val result = getCharacters(lastLoadedPage + 1)) {
                is Resource.Success -> {
                    lastLoadedPage = result.data.page
                    _uiState.update {
                        it.copy(
                            characters = it.characters + result.data.characters,
                            isLoading = false,
                            isLoadingMore = false,
                            canLoadMore = result.data.hasNextPage,
                            isOffline = result.fromCache
                        )
                    }
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, isLoadingMore = false, errorMessage = result.message)
                }
            }
        }
    }
}
