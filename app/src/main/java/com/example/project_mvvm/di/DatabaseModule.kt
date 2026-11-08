package com.example.project_mvvm.di

import android.content.Context
import androidx.room.Room
import com.example.project_mvvm.data.local.MortyDexDatabase
import com.example.project_mvvm.data.local.dao.CharacterDao
import com.example.project_mvvm.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Le enseña a Hilt a crear la base de datos Room y sus DAOs.
 *
 * La base es `@Singleton`: abrir SQLite es caro y tener dos instancias abiertas sobre el
 * mismo archivo puede dar datos inconsistentes.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * `@ApplicationContext`: Hilt pasa el contexto de la app (no el de una Activity), que vive
     * todo lo que vive la app. Room lo usa para ubicar el archivo `mortydex.db`.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MortyDexDatabase =
        Room.databaseBuilder(context, MortyDexDatabase::class.java, MortyDexDatabase.NAME)
            .build()

    @Provides
    fun provideCharacterDao(db: MortyDexDatabase): CharacterDao = db.characterDao()

    @Provides
    fun provideFavoriteDao(db: MortyDexDatabase): FavoriteDao = db.favoriteDao()
}
