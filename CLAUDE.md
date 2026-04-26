# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# PocketDev - Android AI Programming Assistant

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew installDebug           # Build and install to device
./gradlew ktlintCheck            # Run Kotlin lint
./gradlew test                   # Run unit tests
./gradlew test --tests "pkg.ClassName"  # Run single test class
./gradlew kspDebugKotlin         # Generate Hilt/Room sources
./gradlew assembleRelease        # Build release APK
```

## Tech Stack

| Category | Technology |
|---------|------------|
| **Platform** | Android 8.0+ (API 26+) |
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 + DynamicColors |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt |
| **Network** | Retrofit + OkHttp + DynamicHostInterceptor |
| **Async** | Coroutines + Flow |
| **Storage** | DataStore + Room |
| **JSON** | Kotlinx Serialization |

## Architecture

### Layer Dependencies (Critical)

```
UI → Domain ← Data
```

- **Domain layer** has NO Android framework dependencies, no Retrofit, no DataStore
- **Data layer** implements interfaces defined in Domain layer
- **UI layer** depends only on Domain

### DynamicHostInterceptor

Key architectural component enabling runtime AI provider switching:
- Reads `baseUrl`/`apiKey` from DataStore
- Replaces request URL scheme/host dynamically
- Injects Authorization headers
- Supports OpenAI, Anthropic, Gemini, DeepSeek, Ollama formats

### Package Structure

```
com.pocketdev/
├── agent/core/           # Agent, AgentExecutor, AgentLoopOperator
├── agent/protocol/       # AgentRequest, AgentResponse, Finding types
├── data/
│   ├── di/              # Hilt modules
│   ├── local/           # DataStore
│   ├── remote/api/      # Retrofit interfaces
│   └── repository/       # Repository implementations
├── domain/
│   ├── model/           # Domain models (no Android dependencies)
│   ├── repository/      # Repository interfaces
│   └── usecase/         # Business logic
└── ui/
    ├── components/      # Reusable Compose components
    ├── screens/         # Screen composables
    └── navigation/      # NavHost, Screen routes
```

## Key Patterns

### Immutability (Enforced)

```kotlin
// WRONG - mutates state
state.messages.add(newMessage)

// CORRECT - creates new state
state.copy(messages = state.messages + newMessage)
```

### Agent Implementation

```kotlin
class MyAgent : Agent {
    override val type = AgentType.MY_AGENT

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is MyRequest)
        return MyResponse(requestId = request.id, success = true, findings = findings)
    }
}
```

### Screen Navigation

Routes defined in `Screen.kt` as sealed class:
```kotlin
data object Chat : Screen("chat")
data object RepoDetail : Screen("repo/{repoFullName}/{repoId}/{repoOwner}/{repoDefaultBranch}")
```

## AI Provider Configuration

| Provider | Base URL | Note |
|----------|----------|------|
| DeepSeek | `https://api.deepseek.com` | |
| OpenAI | `https://api.openai.com/v1` | |
| Ollama | `http://10.0.2.2:11434` | Use `10.0.2.2` for Android emulator (not `127.0.0.1`) |

## PC CLI (cli/)

Python/FastAPI server for remote file editing and git operations:
```bash
pip install pocketdev-cli
pocketdev-cli serve --port 8080 --api-key your-key
```

API endpoints: `/api/files/read`, `/api/files/write`, `/api/git/commit`, `/api/shell/execute`
