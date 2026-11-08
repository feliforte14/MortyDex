package com.example.project_mvvm.di

import com.example.project_mvvm.data.repository.CharacterRepositoryImpl
import com.example.project_mvvm.domain.repository.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Conecta la interfaz de `domain` con su implementación de `data`.
 *
 * Los casos de uso piden un [CharacterRepository] (una interfaz, que no se puede instanciar).
 * `@Binds` le dice a Hilt: "cuando pidan un `CharacterRepository`, entregá un
 * `CharacterRepositoryImpl`". Es el único lugar de la app que conoce las dos cosas.
 *
 * Es `abstract` porque `@Binds` no necesita cuerpo: Hilt ya sabe construir
 * `CharacterRepositoryImpl` gracias a su `@Inject constructor`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository
}
