package com.example.project_mvvm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de arranque de la app. `@HiltAndroidApp` hace que Hilt genere el contenedor de
 * dependencias cuando se inicia el proceso.
 *
 * Tiene que estar registrada en `AndroidManifest.xml` con `android:name=".MortyDexApplication"`.
 */
@HiltAndroidApp
class MortyDexApplication : Application()
