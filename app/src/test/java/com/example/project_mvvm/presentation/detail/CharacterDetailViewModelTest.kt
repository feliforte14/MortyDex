package com.example.project_mvvm.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.core.model.UiState
import com.example.project_mvvm.core.navigation.Screen
import com.example.project_mvvm.domain.usecase.GetCharacterDetailUseCase
import com.example.project_mvvm.domain.usecase.ObserveIsFavoriteUseCase
import com.example.project_mvvm.domain.usecase.ToggleFavoriteUseCase
import com.example.project_mvvm.fakes.FakeCharacterRepository
import com.example.project_mvvm.fakes.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Tests del ViewModel del detalle. El id se pasa con un SavedStateHandle armado a mano. */
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCharacterRepository()

    private fun createViewModel(id: Int = 1) = CharacterDetailViewModel(
        getCharacterDetail = GetCharacterDetailUseCase(repository),
        toggleFavorite = ToggleFavoriteUseCase(repository),
        observeIsFavorite = ObserveIsFavoriteUseCase(repository),
        savedStateHandle = SavedStateHandle(mapOf(Screen.ARG_CHARACTER_ID to id))
    )

    @Test
    fun `carga el detalle del id recibido por navegacion`() {
        repository.detailResult = Resource.Success(FakeCharacterRepository.detail(1))

        val state = createViewModel().uiState.value

        assertEquals(FakeCharacterRepository.detail(1), (state as UiState.Success).data)
    }

    @Test
    fun `error del repositorio se muestra como UiState Error`() {
        repository.detailResult = Resource.Error("No existe un personaje con id 1")

        val state = createViewModel().uiState.value

        assertEquals("No existe un personaje con id 1", (state as UiState.Error).message)
    }

    @Test
    fun `detalle desde la cache marca offline`() {
        repository.detailResult = Resource.Success(FakeCharacterRepository.detail(1), fromCache = true)

        assertTrue(createViewModel().isOffline.value)
    }

    @Test
    fun `tocar el corazon guarda el personaje en favoritos`() = runTest {
        repository.detailResult = Resource.Success(FakeCharacterRepository.detail(1))
        val viewModel = createViewModel()
        assertFalse(repository.observeIsFavorite(1).first())

        viewModel.onToggleFavorite()

        assertTrue(repository.observeIsFavorite(1).first())
    }
}
