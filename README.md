<p align="center">
  <h1 align="center">PocketDev</h1>
  <p align="center">
    <em>AI‑Powered Mobile Coding Assistant · Multi‑Agent Architecture · Dynamic Provider Switching</em>
  </p>
</p>

<p align="center">
  <a href="https://developer.android.com/"><img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android" alt="Android"></a>
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin"></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Compose"></a>
  <a href="https://dagger.dev/hilt/"><img src="https://img.shields.io/badge/DI-Hilt-2C2D3A?style=flat&logo=google&logoColor=white" alt="Hilt"></a>
  <br>
  <a href="https://github.com/settings"><img src="https://img.shields.io/badge/API-26%2B-4CAF50?style=flat" alt="API 26+"></a>
  <a href="https://github.com"><img src="https://img.shields.io/badge/AI_Providers-8+-FF6B6B?style=flat" alt="8+ AI Providers"></a>
  <a href="./SPEC.md"><img src="https://img.shields.io/badge/Architecture-Clean-6C5CE7?style=flat" alt="Clean Architecture"></a>
  <a href="./LICENSE"><img src="https://img.shields.io/badge/License-MIT-F5DEB3?style=flat" alt="MIT"></a>
</p>

---

## ✨ Core Differentiators

| | |
|---|---|
| **Multi‑Agent System** | Planner · CodeReviewer · SecurityReviewer · TDDGuide · BuildErrorResolver · RefactorCleaner · DocUpdater · E2ERunner — orchestrated via `AgentExecutor` + autonomous `AgentLoopOperator` |
| **Dynamic Provider Switching** | Hot‑swap 8+ AI providers (DeepSeek, OpenAI, Anthropic, Gemini, MiniMax, Kimi, GLM, Ollama, PC CLI) at runtime via `DynamicHostInterceptor` — no app restart required |
| **Mobile‑First Development** | Full development cycle on device — chat with AI, edit code, run Gradle builds, manage git, execute terminal commands. Extend with PC CLI for remote file operations from your phone |
| **Real‑time Collaboration** | CRDT‑like text operations with cursor tracking, conflict resolution, and branch‑aware collaborative editing sessions |

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                     PocketDev Architecture                   │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│   ┌──────────────────┐     ┌──────────────┐                  │
│   │      UI          │     │   Domain     │                  │
│   │  ┌────────────┐  │     │  ┌────────┐  │     ┌──────────┐ │
│   │  │ Compose    ├──┼─────┼─▶│ Models │  │     │  Data    │ │
│   │  │ Screens    │  │     │  ├────────┤  │◀────│────────  │ │
│   │  │ Components │  │     │  │UseCases│  │     │ Remote   │ │
│   │  │ Navigation │  │     │  ├────────┤  │     │ Local    │ │
│   │  │ Theme      │  │     │  │  Repo  │  │     │ Repo Impl│ │
│   │  └────────────┘  │     │  └────────┘  │     └──────────┘ │
│   └──────────────────┘     └──────┬───────┘                  │
│                                   │                           │
│          ┌────────────────────────┘                           │
│          │                                                    │
│   ┌──────▼───────────────────────────────────────────┐       │
│   │              Agent System                          │       │
│   │   ┌────────────┐ ┌──────────┐ ┌───────────────┐  │       │
│   │   │  Planner   │ │CodeReview│ │ SecurityReview│  │       │
│   │   ├────────────┤ ├──────────┤ ├───────────────┤  │       │
│   │   │  TDDGuide  │ │BuildError│ │RefactorCleaner│  │       │
│   │   ├────────────┤ ├──────────┤ ├───────────────┤  │       │
│   │   │ DocUpdater │ │ E2ERunner│ │    Search     │  │       │
│   │   └────────────┘ └──────────┘ └───────────────┘  │       │
│   │   AgentExecutor · AgentLoopOperator               │       │
│   │   suspend fun execute(request: AgentRequest):      │       │
│   │       AgentResponse                                │       │
│   └────────────────────────────────────────────────────┘       │
│                                                               │
│   ┌────────────────────────────────────────────────────────┐  │
│   │              DynamicHostInterceptor                     │  │
│   │                                                        │  │
│   │   ┌──────────┐    ┌──────────────┐    ┌────────────┐  │  │
│   │   │ Retrofit │───▶│ Interceptor  │───▶│  Provider  │  │  │
│   │   │ Request  │    │ DynamicHost  │    │    API     │  │  │
│   │   └──────────┘    └──────┬───────┘    └────────────┘  │  │
│   │                          │                             │  │
│   │                   ┌──────▼──────┐                     │  │
│   │                   │ DataStore   │                     │  │
│   │                   │ Provider    │                     │  │
│   │                   │ Config      │                     │  │
│   │                   └─────────────┘                     │  │
│   │                                                        │  │
│   │   when (provider.type) {                               │  │
│   │     DEEPSEEK, OPENAI → interceptOpenAiStyle()         │  │
│   │     ANTHROPIC       → interceptAnthropic()            │  │
│   │     GEMINI          → interceptGemini()               │  │
│   │     OLLAMA          → interceptOllama()               │  │
│   │   }                                                    │  │
│   └────────────────────────────────────────────────────────┘  │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

### Layer Dependencies (strict)

```
     UI ──▶ Domain ◀── Data
```

- **Domain** — Pure Kotlin, zero Android framework dependencies. No Retrofit, no DataStore.
- **Data** — Implements domain repository interfaces. Holds Retrofit, Room, DataStore.
- **UI** — Jetpack Compose screens depending only on domain models and use cases.

---

## 🤖 Multi‑Agent System

PocketDev ships a production‑grade multi‑agent engine inspired by Anthropic's agent protocol. Each agent implements a common interface:

```kotlin
interface Agent {
    val type: AgentType
    suspend fun execute(request: AgentRequest): AgentResponse
}
```

| Agent | Type | Responsibility |
|-------|------|---------------|
| **Planner** | `PLANNER` | Phased implementation plans with risk assessment |
| **CodeReviewer** | `CODE_REVIEWER` | Code quality analysis, bug detection, improvement suggestions |
| **SecurityReviewer** | `SECURITY_REVIEWER` | Vulnerability scanning, OWASP compliance |
| **TDDGuide** | `TDD_GUIDE` | Test‑driven development workflow enforcement |
| **BuildErrorResolver** | `BUILD_ERROR_RESOLVER` | Compilation error diagnosis and fixes |
| **RefactorCleaner** | `REFACTOR_CLEANER` | Dead code detection, code consolidation |
| **DocUpdater** | `DOC_UPDATER` | Automated documentation generation |
| **E2ERunner** | `E2E_RUNNER` | End‑to‑end test execution and reporting |
| **Search** | `SEARCH` | Codebase search and exploration |
| **General** | `GENERAL` | Fallback general‑purpose agent |

### Autonomous Loop

The `AgentLoopOperator` implements a **Think‑Act‑Observe‑Decide** cycle for complex multi‑step tasks:

```
  ┌─────────────────────────────────────┐
  │  Think                              │
  │  ┌───────────────────────────────┐  │
  │  │ Analyze goal, plan next step  │  │
  │  └───────────────┬───────────────┘  │
  │                  ▼                   │
  │  Act                                 │
  │  ┌───────────────────────────────┐  │
  │  │ Execute agent, process files  │  │
  │  └───────────────┬───────────────┘  │
  │                  ▼                   │
  │  Observe                             │
  │  ┌───────────────────────────────┐  │
  │  │ Collect results, check state  │  │
  │  └───────────────┬───────────────┘  │
  │                  ▼                   │
  │  Decide                              │
  │  ┌───────────────────────────────┐  │
  │  │ Complete? → return            │  │
  │  │ More steps? → loop            │  │
  │  └───────────────┬───────────────┘  │
  │                  │                   │
  └──────────────────┼───────────────────┘
                     │
              Flow<LoopProgress>
         (Thinking → Acting → Observing → Completed)
```

### Subagent Execution

Agents can spawn hierarchical sub‑agents for parallel task execution, each with isolated context. The `SubagentExecutor` manages lifecycle, context propagation, and result aggregation.

---

## 🔄 DynamicHostInterceptor

The core networking innovation — a custom OkHttp interceptor that enables **runtime AI provider switching** without application restart.

### Request Flow

```
  ┌──────────────┐    ┌───────────────────┐    ┌──────────────────┐
  │  Retrofit    │───▶│  DynamicHost      │───▶│  Provider API    │
  │  Request     │    │  Interceptor       │    │  Server          │
  └──────────────┘    └────────┬──────────┘    └──────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │  DataStore          │
                    │  ┌──────────────┐  │
                    │  │ baseUrl      │  │
                    │  │ apiKey       │  │
                    │  │ providerType │  │
                    │  └──────────────┘  │
                    └───────────────────┘
```

### Provider Authentication Matrix

| Provider | Auth Header | Key Format | Base URL |
|----------|------------|------------|----------|
| **DeepSeek** | `Authorization: Bearer <key>` | API Key | `https://api.deepseek.com` |
| **OpenAI** | `Authorization: Bearer <key>` | API Key | `https://api.openai.com/v1` |
| **Anthropic** | `x-api-key: <key>` | API Key | `https://api.anthropic.com` |
| **Gemini** | Query param `?key=<key>` | API Key | `https://generativelanguage.googleapis.com` |
| **MiniMax** | `Authorization: Bearer <key>` | API Key | MiniMax endpoint |
| **Kimi** | `Authorization: Bearer <key>` | API Key | Kimi endpoint |
| **GLM** | `Authorization: Bearer <key>` | API Key | GLM endpoint |
| **Ollama** | None | — | `http://10.0.2.2:11434` |
| **PC CLI** | Custom header | API Key | Configurable |

> **Note:** `10.0.2.2` is used for Android emulator localhost routing. On physical devices, use the machine's LAN IP.

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Platform** | Android 8.0+ (API 26+) | Minimum SDK target |
| **Language** | Kotlin | Primary development language |
| **UI** | Jetpack Compose + Material 3 + DynamicColors | Declarative UI with adaptive theming |
| **Architecture** | MVVM + Clean Architecture | Strict layer separation |
| **DI** | Hilt (Dagger) | Compile‑time dependency injection |
| **Network** | Retrofit + OkHttp + DynamicHostInterceptor | Multi‑provider HTTP layer |
| **Async** | Coroutines + Flow | Structured concurrency |
| **Storage** | DataStore (Preferences) + Room | Key‑value + relational persistence |
| **Serialization** | Kotlinx Serialization | Type‑safe JSON parsing |
| **Build** | Gradle Kotlin DSL + Version Catalog | Modern Gradle configuration |

---

## 🚀 Quick Start

```bash
# 1. Clone & open
git clone https://github.com/your/pocketdev.git
# Open in Android Studio (or use CLI)

# 2. Build debug APK
./gradlew assembleDebug

# 3. Install on device / emulator
./gradlew installDebug
```

**Prerequisites:** Android SDK (API 34), JDK 17+, Gradle 8.5 (wrapper included)

---

## 📊 AI Provider Configuration

Providers are configured at runtime through the Settings UI. The configuration is persisted in DataStore and consumed by `DynamicHostInterceptor` on each request.

| Provider | Default Model | Emulator URL | Physical Device URL |
|----------|--------------|--------------|-------------------|
| DeepSeek | `deepseek-chat` | `https://api.deepseek.com` | Same |
| OpenAI | `gpt-4` | `https://api.openai.com/v1` | Same |
| Anthropic | `claude-opus-4` | `https://api.anthropic.com` | Same |
| Gemini | `gemini-pro` | `https://generativelanguage.googleapis.com` | Same |
| MiniMax | MiniMax model | Vendor URL | Same |
| Kimi | Kimi model | Vendor URL | Same |
| GLM | GLM model | Vendor URL | Same |
| Ollama | `llama2` | `http://10.0.2.2:11434` | `http://<lan-ip>:11434` |

---

## 📱 Screens

| Screen | Core Feature |
|--------|-------------|
| **Chat** | AI conversation with markdown rendering, code cards, and push‑to‑GitHub |
| **Code Editor** | Syntax highlighting, multi‑tab editing, search & replace |
| **Remote Editor** | GitHub file editing with SHA verification |
| **Repos** | GitHub/GitLab repository browser (OAuth 2.0) |
| **Ollama** | Local model management — download, list, delete |
| **PC Connection** | Remote CLI server connectivity via PocketDev CLI |
| **Build** | Gradle build execution with real‑time progress |
| **Terminal** | Local/remote shell command execution |
| **Settings** | AI provider config, theme, editor preferences, build settings |

---

## 🖥️ PC CLI (Remote Operations)

A Python/FastAPI companion server enabling remote file editing and git operations from your mobile device:

```bash
pip install pocketdev-cli
pocketdev-cli serve --port 8080 --api-key your-key
```

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/system/info` | GET | Remote system information |
| `/api/files/read` | POST | Read file from remote filesystem |
| `/api/files/write` | POST | Write file to remote filesystem |
| `/api/git/commit` | POST | Git commit with file list |
| `/api/shell/execute` | POST | Execute shell command |
| `/api/ollama/chat` | POST | Proxy to local Ollama instance |
| `/health` | GET | Connection health check |

---

## 📚 Documentation

| Document | Contents |
|----------|----------|
| [CLAUDE.md](CLAUDE.md) | Build commands, architecture guidance, package structure, key patterns |
| [SPEC.md](SPEC.md) | Detailed technical specification |
| [cli/README.md](cli/README.md) | PC CLI tool documentation |

---

## 📄 License

```
MIT License

Copyright (c) 2026 PocketDev
```

---

<p align="center">
  <sub>Built with Kotlin · Jetpack Compose · Hilt · Clean Architecture</sub>
</p>
