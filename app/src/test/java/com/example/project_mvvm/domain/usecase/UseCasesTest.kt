package com.example.project_mvvm.domain.usecase

import com.example.project_mvvm.core.model.Resource
import com.example.project_mvvm.fakes.FakeCharacterRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests de las reglas de los casos de uso, usando el repositorio falso.
 * `runTest` permite llamar funciones `suspend` dentro del test.
 */
class UseCasesTest {

    private val repository = FakeCharacterRepository()

    @Test
    fun `pagina menor a 1 da error sin consultar el repositorio`() = runTest {
        val result = GetCharactersUseCase(repository)(0)
        assertTrue(result is Resource.Error)
        assertTrue(repository.requestedPages.isEmpty())
    }

    @Test
    fun `pagina valida se le pide al repositorio`() = runTest {
        repository.pages[2] = FakeCharacterRepository.page(2, 21..40, hasNext = true)
        val result = GetCharactersUseCase(repository)(2)
        assertEquals(listOf(2), repository.requestedPages)
        assertEquals(20, (result as Resource.Success).data.characters.size)
    }

    @Test
    fun `id menor a 1 da error sin consultar el repositorio`() = runTest {
        val result = GetCharacterDetailUseCase(repository)(-5)
        assertEquals("ID de personaje inválido: -5", (result as Resource.Error).message)
        assertEquals(0, repository.detailRequests)
    }

    @Test
    fun `toggle agrega y quita de favoritos`() = runTest {
        val toggle = ToggleFavoriteUseCase(repository)
        val isFavorite = ObserveIsFavoriteUseCase(repository)
        val character = FakeCharacterRepository.summary(7)

        toggle(character)
        assertTrue(isFavorite(7).first())
        assertEquals(listOf(character), ObserveFavoritesUseCase(repository)().first())

        toggle(character)
        assertFalse(isFavorite(7).first())
    }
}
