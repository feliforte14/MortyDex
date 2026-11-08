package com.example.project_mvvm.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.project_mvvm.data.local.dao.CharacterDao
import com.example.project_mvvm.data.local.dao.FavoriteDao
import com.example.project_mvvm.data.local.entity.CharacterEntity
import com.example.project_mvvm.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentados de Room: corren en un emulador o teléfono, porque necesitan SQLite real.
 *
 * Se usa una base **en memoria**: se crea vacía antes de cada test y desaparece al cerrarla,
 * así los tests no dependen unos de otros ni tocan los datos reales de la app.
 *
 * Correr con: `./gradlew :app:connectedDebugAndroidTest`
 */
@RunWith(AndroidJUnit4::class)
class MortyDexDatabaseTest {

    private lateinit var db: MortyDexDatabase
    private lateinit var characterDao: CharacterDao
    private lateinit var favoriteDao: FavoriteDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MortyDexDatabase::class.java
        ).build()
        characterDao = db.characterDao()
        favoriteDao = db.favoriteDao()
    }

    @After
    fun tearDown() = db.close()

    private fun character(id: Int, page: Int, name: String = "Personaje $id") = CharacterEntity(
        id = id, page = page, name = name, status = "ALIVE", species = "Human", gender = "Male",
        origin = "Earth", location = "Citadel", imageUrl = "url$id", episodeCount = 1
    )

    private fun favorite(id: Int, addedAt: Long) =
        FavoriteEntity(id = id, name = "Personaje $id", status = "DEAD", species = "Alien", imageUrl = "url$id", addedAt = addedAt)

    @Test
    fun cachePorPaginaYPorId() = runTest {
        characterDao.upsertAll(listOf(character(2, page = 1), character(1, page = 1), character(21, page = 2)))

        assertEquals(listOf(1, 2), characterDao.getByPage(1).map { it.id }) // ordenados por id
        assertEquals(listOf(21), characterDao.getByPage(2).map { it.id })
        assertEquals("Personaje 21", characterDao.getById(21)?.name)
        assertNull(characterDao.getById(99))
    }

    @Test
    fun upsertActualizaEnLugarDeDuplicar() = runTest {
        characterDao.upsertAll(listOf(character(1, page = 1, name = "Viejo")))
        characterDao.upsertAll(listOf(character(1, page = 1, name = "Nuevo")))

        val page = characterDao.getByPage(1)
        assertEquals(1, page.size)
        assertEquals("Nuevo", page.first().name)
    }

    @Test
    fun favoritosAgregarListarYQuitar() = runTest {
        favoriteDao.insert(favorite(1, addedAt = 100))
        favoriteDao.insert(favorite(2, addedAt = 200))

        // Del más reciente al más viejo.
        assertEquals(listOf(2, 1), favoriteDao.observeAll().first().map { it.id })
        assertTrue(favoriteDao.isFavorite(1))
        assertTrue(favoriteDao.observeIsFavorite(1).first())

        favoriteDao.deleteById(1)

        assertFalse(favoriteDao.isFavorite(1))
        assertEquals(listOf(2), favoriteDao.observeAll().first().map { it.id })
    }

    @Test
    fun unPersonajeNoPuedeEstarDosVecesEnFavoritos() = runTest {
        favoriteDao.insert(favorite(1, addedAt = 100))
        favoriteDao.insert(favorite(1, addedAt = 300))

        val all = favoriteDao.observeAll().first()
        assertEquals(1, all.size)
        assertEquals(300L, all.first().addedAt)
    }
}
