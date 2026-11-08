package com.example.project_mvvm.di

import com.example.project_mvvm.data.remote.RickAndMortyApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Le enseña a Hilt a crear todo lo de red.
 *
 * - `@Module`: esta clase contiene "recetas" para crear objetos.
 * - `@InstallIn(SingletonComponent::class)`: las recetas viven mientras viva la app.
 * - `@Provides`: "cuando alguien pida un X, crealo con esta función".
 * - `@Singleton`: se crea una sola vez y se reutiliza (crear Retrofit es caro).
 *
 * Hilt arma la cadena solo: para dar un [RickAndMortyApi] necesita un [Retrofit], que necesita
 * un [OkHttpClient] y un [Moshi], y busca acá cómo crear cada uno.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    /**
     * Moshi convierte JSON ⇄ objetos Kotlin. No agregamos `KotlinJsonAdapterFactory`
     * (reflexión) porque los DTOs usan adapters generados por KSP (`@JsonClass`).
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    /**
     * OkHttp es quien hace las llamadas HTTP de verdad. El interceptor de log escribe cada
     * pedido en Logcat (filtrar por "okhttp"); BASIC muestra método, URL, código y tiempo.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /** Retrofit une la dirección base, el cliente HTTP y el conversor de JSON. */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /** Retrofit genera la implementación de la interfaz [RickAndMortyApi]. */
    @Provides
    @Singleton
    fun provideRickAndMortyApi(retrofit: Retrofit): RickAndMortyApi =
        retrofit.create(RickAndMortyApi::class.java)
}
