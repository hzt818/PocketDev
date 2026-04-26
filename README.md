# PocketDev Android

AI-powered Android programming assistant with dynamic multi-provider support.

## Build Instructions

### Prerequisites

1. **Android SDK** (API 34)
2. **JDK 17+**
3. **Gradle 8.5** (or use the wrapper)

### Setup

1. Clone the repository
2. Open in Android Studio (or command line)
3. Generate the Gradle wrapper:
   ```bash
   gradle wrapper --gradle-version=8.5
   ```
4. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```

### Architecture

This project follows **Clean Architecture** with **MVVM**:

```
com.pocketdev/
├── data/                    # Data Layer
│   ├── di/                  # Hilt Modules
│   ├── local/               # DataStore
│   ├── remote/              # Retrofit APIs & Interceptors
│   └── repository/          # Repository Implementations
├── domain/                  # Domain Layer
│   ├── model/               # Domain Models
│   ├── repository/          # Repository Interfaces
│   └── usecase/             # Business Logic
└── ui/                      # Presentation Layer
    ├── components/          # Reusable Compose Components
    ├── navigation/          # Navigation
    ├── screens/             # Screen Composables
    └── theme/               # Material 3 Theme
```

## Features

### Phase 1: Scaffolding
- Kotlin DSL with Version Catalog
- Hilt dependency injection
- Jetpack Compose with Material 3
- Dynamic color support

### Phase 2: Dynamic Network Layer
- `DynamicHostInterceptor` for runtime Base URL switching
- Support for DeepSeek, OpenAI, and Ollama
- DataStore-based configuration

### Phase 3: GitHub Integration
- OAuth 2.0 authentication via Chrome Custom Tabs
- Repository browsing
- File commit with SHA verification

### Phase 4: AI Chat
- Structured JSON responses from AI
- Markdown rendering
- Code Card UI with Push to GitHub

## Configuration

### AI Providers

| Provider | Base URL | Model |
|----------|----------|-------|
| DeepSeek | https://api.deepseek.com | deepseek-chat |
| OpenAI | https://api.openai.com/v1 | gpt-4 |
| Ollama | http://10.0.2.2:11434 | llama2 |

### Ollama with Android Emulator

When using Ollama with Android Emulator, use `10.0.2.2` instead of `127.0.0.1`:
```
http://10.0.2.2:11434
```

## License

MIT
