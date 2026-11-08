# MortyDex

App Android de práctica que lista los personajes de Rick and Morty (API pública
[rickandmortyapi.com](https://rickandmortyapi.com)), con detalle, **favoritos persistentes** y
**caché offline** con Room.

Está armada con **MVVM + Clean Architecture**, siguiendo la misma organización que la Pokedex
de referencia (`PokedexCleanArchitecture`), y le suma lo que la Pokedex no tiene: persistencia con Room.

| Lista | Detalle | Sin conexión |
|---|---|---|
| Scroll infinito (20 por página) | Foto, estado, origen, ubicación y episodios; ❤️ para favoritos | Muestra lo último descargado y lo avisa |

## Stack

| Para qué | Librería |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Inyección de dependencias | Hilt (con KSP) |
| Red | Retrofit + Moshi (adapters generados con KSP) + OkHttp logging |
| Base de datos local | Room (KSP), con esquema exportado en `app/schemas/` |
| Imágenes | Coil |
| Asincronía | Coroutines + Flow / StateFlow |
| Tests | JUnit 4, kotlinx-coroutines-test, AndroidX Test (Room en memoria) |

Versiones: AGP 9.3.3, Kotlin 2.2.10 (Kotlin integrado en AGP 9, sin el plugin `kotlin-android`),
KSP 2.3.13, Hilt 2.60.1, Room 2.8.4, compileSdk/targetSdk 37, minSdk 24.

## Arquitectura

```
presentation  ──►  domain  ◄──  data
 (Compose,         (Kotlin        (Retrofit,
  ViewModel)        puro)          Room)
```

**Regla de oro:** las dependencias apuntan hacia `domain`. `domain` no conoce Android, Retrofit
ni Room; define *qué* datos existen y *qué* se puede pedir. `data` decide *cómo* conseguirlos y
`presentation` solo habla con los casos de uso.

```
com.example.project_mvvm
├── MainActivity.kt              Única Activity: tema + MortyDexApp
├── MortyDexApplication.kt       @HiltAndroidApp: arranca Hilt
├── core/
│   ├── model/                   Resource (resultado de data) y UiState (estado de pantalla)
│   ├── navigation/              Screen: rutas de navegación
│   └── theme/                   Colores, tipografía y tema
├── domain/
│   ├── model/                   CharacterSummary, CharacterDetail, CharacterPage, CharacterStatus
│   ├── repository/              CharacterRepository (interfaz = "menú")
│   └── usecase/                 GetCharacters, GetCharacterDetail, ObserveFavorites,
│                                ObserveIsFavorite, ToggleFavorite
├── data/
│   ├── remote/                  RickAndMortyApi (Retrofit) + dto/ (forma del JSON)
│   ├── local/                   MortyDexDatabase + dao/ + entity/ (Room)
│   ├── mapper/                  CharacterMapper: DTO / Entity ⇄ domain
│   └── repository/              CharacterRepositoryImpl: red primero, caché de respaldo
├── di/                          NetworkModule, DatabaseModule, RepositoryModule (Hilt)
└── presentation/
    ├── MortyDexApp.kt           NavHost con las 3 pantallas
    ├── common/components/       Tarjeta, carga, error, vacío, banner offline
    ├── characters/              Lista paginada (ViewModel + Screen)
    ├── detail/                  Detalle + favorito (ViewModel + Screen)
    └── favorites/               Favoritos guardados (ViewModel + Screen)
```

### Recorrido de un pedido

```
CharacterListScreen ──loadNextPage()──► CharacterListViewModel
      ▲                                        │ getCharacters(page)
      │ StateFlow<CharacterListUiState>        ▼
      │                                GetCharactersUseCase  (regla: page ≥ 1)
      │                                        │
      │                                        ▼
      │                            CharacterRepository (interfaz, domain)
      │                                        │  Hilt entrega CharacterRepositoryImpl
      │                                        ▼
      │                       ┌──── api.getCharacters(page) ────┐
      │                       │ OK: guardar en Room y devolver  │
      │                       │ sin red: leer de Room (fromCache)│
      │                       └─────────────────────────────────┘
      └──────────── Resource.Success / Resource.Error ◄─┘
```

### Persistencia

| Qué | Dónde | Por qué |
|---|---|---|
| Lista/detalle ya cargados | `ViewModel` | Sobreviven a la rotación |
| Personajes descargados (caché) | Room, tabla `characters` | Ver la app sin internet |
| Favoritos | Room, tabla `favorites` | Decisión del usuario: sobrevive a cerrar la app |
| Fotos | Caché de Coil | No volver a bajarlas |

- Las tablas se observan con `Flow`: la pantalla de favoritos y el ❤️ se actualizan solos.
- `exportSchema = true` guarda el esquema en `app/schemas/`. Si se cambia una tabla, se sube la
  versión y se escribe una `Migration` (no se usa `fallbackToDestructiveMigration`).

### Manejo de errores

`CharacterRepositoryImpl` nunca deja escapar una excepción: siempre devuelve un `Resource`.

| Situación | Resultado |
|---|---|
| Sin conexión y la página está guardada | Datos de Room + banner "Sin conexión" |
| Sin conexión y no está guardada | "Sin conexión. Revisá tu internet." + Reintentar |
| 404 en el detalle | "No existe un personaje con id N" |
| 429 (demasiados pedidos) | "Demasiados pedidos seguidos..." + Reintentar |
| Otro error HTTP | "Error del servidor (código N)" |

## Cómo ejecutar

```bash
git clone https://github.com/feliforte14/MortyDex.git
```

1. Abrir en Android Studio y esperar el Gradle sync.
2. **Run ▶** en un emulador o teléfono (Android 7.0+).
3. Para probar la caché: abrir la app con internet, activar el modo avión, cerrar la app desde
   Recientes y volver a abrirla.
4. Para ver la base: **View ▸ Tool Windows ▸ App Inspection ▸ Database Inspector**.
5. Para ver las llamadas a la API: Logcat filtrando por `okhttp`.

## Tests

```bash
./gradlew :app:testDebugUnitTest            # 18 tests unitarios (JVM)
./gradlew :app:connectedDebugAndroidTest    # 4 tests de Room (necesita emulador)
```

| Test | Qué verifica |
|---|---|
| `CharacterMapperTest` | Texto → enum, valores desconocidos, conteo de episodios, ida y vuelta con Room |
| `UseCasesTest` | Reglas de página e id; agregar y quitar favoritos |
| `CharacterListViewModelTest` | Carga inicial, paginación, no repetir pedidos, error + reintentar, modo offline |
| `CharacterDetailViewModelTest` | Id desde la navegación, error, offline, guardar favorito |
| `MortyDexDatabaseTest` | Caché por página e id, upsert sin duplicar, favoritos ordenados y sin repetidos |

Los tests unitarios usan `FakeCharacterRepository`: como todo depende de la interfaz
`CharacterRepository`, se reemplaza por una versión falsa sin red ni base de datos.

## Diferencias con la Pokedex

- **Room**: caché offline y favoritos (la Pokedex solo usa red).
- **Paginación** con scroll infinito y `CharacterPage` en `domain`.
- **Moshi sin reflexión**: no se registra `KotlinJsonAdapterFactory` porque los DTOs usan `@JsonClass` (KSP).
- Solo `StateFlow` (la Pokedex compara LiveData y Flow con fines didácticos).
- Versiones más nuevas: AGP 9 y KSP 2.3 (con AGP 9 no se agrega el plugin `kotlin-android`).
