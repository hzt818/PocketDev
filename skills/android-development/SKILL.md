---
name: android-development
description: Android development best practices, Jetpack Compose, and Kotlin patterns for PocketDev
origin: pocketdev
tags:
  - android
  - jetpack-compose
  - kotlin
  - material3
  - clean-architecture
  - mvvm
---

# Android Development

Comprehensive Android development skill for building production-quality apps with PocketDev.

## When to Activate

- User mentions "Android", "Jetpack Compose", "Kotlin"
- User is working on `app/` directory in an Android project
- User asks about Android patterns, Material Design, or architecture
- User invokes `/android-development` or `/android`

## Project Structure

```
app/
├── src/main/
│   ├── java/com/pocketdev/
│   │   ├── data/           # Data layer (repositories, APIs, DI)
│   │   ├── domain/          # Domain layer (models, use cases)
│   │   └── ui/              # Presentation layer (screens, components)
│   └── res/                 # Resources (layouts, strings, themes)
└── build.gradle.kts         # App-level build config
```

## Architecture Patterns

### Clean Architecture Layers

| Layer | Responsibility | Dependencies |
|-------|---------------|--------------|
| **UI** | Compose screens, ViewModels | Domain models |
| **Domain** | Business logic, use cases | No framework deps |
| **Data** | Repositories, APIs, storage | Domain interfaces |

### MVVM + Hilt

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val useCase: MyUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MyUiState())
    val state: StateFlow<MyUiState> = _state.asStateFlow()
}
```

## Key Technologies

### Jetpack Compose

- **BOM**: `2024.02.00`
- **Material 3**: Dynamic colors on Android 12+
- **Navigation**: `2.7.7`

### Dependency Injection

- **Hilt**: `2.50`
- Use `@HiltViewModel`, `@Inject`, `@Module`

### Networking

- **Retrofit**: `2.9.0`
- **OkHttp**: `4.12.0`
- Use interceptors for auth, logging

### Async

- **Kotlin Coroutines**: Structured concurrency
- **Flow**: Reactive streams for UI state

## Best Practices

### Immutability (CRITICAL)

```kotlin
// WRONG - mutates state
state.messages.add(newMessage)

// CORRECT - creates new state
state.copy(messages = state.messages + newMessage)
```

### File Organization

- 200-400 lines typical per file
- 800 lines max
- Organize by feature/domain, not by type

### Error Handling

- Use `Result<T>` for operations that can fail
- Handle errors at every layer
- Provide user-friendly messages in UI

## Common Patterns

### Repository Pattern

```kotlin
interface ItemRepository {
    suspend fun getById(id: String): Result<Item>
    suspend fun getAll(): Result<List<Item>>
    fun observeAll(): Flow<List<Item>>
}
```

### Use Case Pattern

```kotlin
class GetItemUseCase(private val repo: ItemRepository) {
    suspend operator fun invoke(id: String): Result<Item> {
        return repo.getById(id)
    }
}
```

### State Management

```kotlin
data class ScreenState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ScreenEvent {
    data object Load : ScreenEvent
    data class Delete(val id: String) : ScreenEvent
}
```

## Material 3 Theming

```kotlin
@Composable
fun MyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

## Navigation

```kotlin
val navController = rememberNavController()
NavHost(navController, startDestination = "home") {
    composable("home") { HomeScreen() }
    composable("detail/{id}") { backStackEntry ->
        DetailScreen(backStackEntry.arguments?.getString("id"))
    }
}
```

## Build & Run

```bash
# Debug build
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run tests
./gradlew test

# KtLint check
./gradlew ktlintCheck
```

## See Also

- Skill: `kotlin-coroutines-flows`
- Skill: `android-clean-architecture`
- PocketDev project: `e:/pocketdev`
