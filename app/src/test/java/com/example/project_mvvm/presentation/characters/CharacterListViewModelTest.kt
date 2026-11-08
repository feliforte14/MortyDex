package com.example.project_mvvm.presentation.characters

import com.example.project_mvvm.domain.usecase.GetCharactersUseCase
import com.example.project_mvvm.fakes.FakeCharacterRepository
import com.example.project_mvvm.fakes.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests del ViewModel de la lista: verificamos los estados que va a dibujar la pantalla.
 * Con `UnconfinedTestDispatcher` las corrutinas terminan antes de que siga el test.
 */
class CharacterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCharacterRepository()

    private fun createViewModel() = CharacterListViewModel(GetCharactersUseCase(repository))

    @Test
    fun `al crearse carga la pagina 1`() {
        repository.pages[1] = FakeCharacterRepository.page(1, 1..20, hasNext = true)

        val state = createViewModel().uiState.value

        assertEquals(20, state.characters.size)
        assertFalse(state.isLoading)
        assertTrue(state.canLoadMore)
        assertNull(state.errorMessage)
    }

    @Test
    fun `loadNextPage agrega la pagina 2 a la lista`() {
        repository.pages[1] = FakeCharacterRepository.page(1, 1..20, hasNext = true)
        repository.pages[2] = FakeCharacterRepository.page(2, 21..40, hasNext = false)
        val viewModel = createViewModel()

        viewModel.loadNextPage()

        val state = viewModel.uiState.value
        assertEquals((1..40).toList(), state.characters.map { it.id })
        assertFalse(state.canLoadMore)
    }

    @Test
    fun `sin mas paginas no vuelve a pedir`() {
        repository.pages[1] = FakeCharacterRepository.page(1, 1..20, hasNext = false)
        val viewModel = createViewModel()

        viewModel.loadNextPage()
        viewModel.loadNextPage()

        assertEquals(listOf(1), repository.requestedPages)
    }

    @Test
    fun `error en la primera pagina muestra el mensaje y reintentar vuelve a pedirla`() {
        val viewModel = createViewModel() // la página 1 no está configurada → error

        assertEquals("Sin conexión. Revisá tu internet.", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.characters.isEmpty())

        repository.pages[1] = FakeCharacterRepository.page(1, 1..20, hasNext = true)
        viewModel.loadNextPage()

        assertEquals(listOf(1, 1), repository.requestedPages)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(20, viewModel.uiState.value.characters.size)
    }

    @Test
    fun `datos de la cache marcan la pantalla como offline`() {
        repository.pages[1] = FakeCharacterRepository.page(1, 1..20, hasNext = true, fromCache = true)

        assertTrue(createViewModel().uiState.value.isOffline)
    }
}
