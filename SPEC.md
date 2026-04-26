# PocketDev AI - Android 自动化编程助手

## 1. Project Overview

**Project Name:** PocketDev
**Type:** Android Native Application + PC CLI Tool
**Core Functionality:** AI-powered mobile coding assistant with hybrid inference:
- **Cloud AI** - DeepSeek, OpenAI API
- **Local AI** - Embedded Ollama with model downloads (Gemma 4, Llama, etc.)
- **Remote PC Control** - CLI tool for real file editing and git operations from phone

## 2. Technology Stack & Choices

| Category | Choice |
|----------|--------|
| **OS Target** | Android 8.0+ (API 26+) |
| **Language** | Kotlin (Strict Mode) |
| **UI Framework** | Jetpack Compose with Material 3 |
| **Architecture** | MVVM + Clean Architecture (UI → Domain → Data) |
| **Dependency Injection** | Hilt |
| **Networking** | Retrofit + OkHttp with dynamic interceptor |
| **Async** | Coroutines + Flow |
| **Local Storage** | DataStore (Preferences) + Room Database |
| **JSON Parsing** | Kotlinx Serialization |
| **PC CLI** | Python 3 with FastAPI |

## 3. Hybrid Inference Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        PocketDev Android App                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  DeepSeek   │  │   OpenAI    │  │   Ollama    │              │
│  │   Cloud     │  │   Cloud     │  │   Local     │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
│         │                │                │                      │
│         └────────────────┴────────────────┘                      │
│                          │                                       │
│              ┌───────────┴───────────┐                           │
│              │   UnifiedLlmService  │                           │
│              │  (AI Provider Hub)    │                           │
│              └───────────┬───────────┘                           │
│                          │                                       │
│              ┌───────────┴───────────┐                           │
│              │   PC CLI Connector    │                           │
│              │  (Remote File Ops)   │                           │
│              └───────────┬───────────┘                           │
└──────────────────────────┼───────────────────────────────────────┘
                           │
                    ┌──────┴──────┐
                    │  PC CLI Tool │
                    │  (Python)    │
                    │              │
                    │ • File Edit  │
                    │ • Git Ops    │
                    │ • Shell Cmd  │
                    └─────────────┘
```

## 4. AI Provider Configuration

### Cloud Providers
| Provider | Endpoint | Models |
|----------|----------|--------|
| DeepSeek | https://api.deepseek.com | deepseek-chat, deepseek-coder |
| OpenAI | https://api.openai.com/v1 | gpt-4, gpt-3.5-turbo |

### Local Provider (Ollama)
| Feature | Description |
|---------|-------------|
| **Embedded Server** | Ollama binary bundled with app |
| **Model Download** | Gemma 4, Llama 3, Mistral, Codellama, etc. |
| **Port** | Default 11434, configurable |

### Remote PC Provider
| Feature | Description |
|---------|-------------|
| **Connection** | HTTP via local IP or ngrok tunnel |
| **Auth** | API Key or token-based |
| **Operations** | File read/write, git commit, shell commands |

## 5. PC CLI Tool Specification

### Installation
```bash
pip install pocketdev-cli
pocketdev-cli serve --port 8080 --api-key your-secret-key
```

### API Endpoints

```
POST /api/chat/completions    # Proxy to local Ollama or AI provider
GET  /api/files/read?path=    # Read file contents
POST /api/files/write         # Write file (body: {path, content})
POST /api/git/commit          # Git commit (body: {message, files[]})
POST /api/shell/execute       # Run shell command
GET  /api/system/info         # System information
```

### WebSocket Support
```
WS /ws                       # Real-time output streaming
```

## 6. Feature List

### Phase 1 - Scaffolding ✓
- [x] Kotlin DSL project setup with Version Catalog
- [x] Hilt dependency injection configuration
- [x] Jetpack Compose with Material 3 theming
- [x] Dynamic color support (wallpaper-based theming)
- [x] Clean Architecture package structure

### Phase 2 - Dynamic Network Layer ✓
- [x] UserSettingsRepository with DataStore
- [x] DynamicHostInterceptor for Base URL switching
- [x] LlmApiService with OpenAI-compatible interface
- [x] Support for DeepSeek, OpenAI, and Ollama

### Phase 3 - GitHub Integration ✓
- [x] GitHub OAuth 2.0 authentication (Chrome Custom Tabs)
- [x] GithubAuthManager for token management
- [x] GitHubApi for repository operations
- [x] CommitFileUseCase with SHA verification flow

### Phase 4 - AI Chat Interface ✓
- [x] ChatScreen with message bubbles
- [x] SYSTEM_PROMPT enforcing JSON responses
- [x] Markdown rendering for explanations
- [x] Code Card UI for code actions
- [x] Push to GitHub functionality

### Phase 5 - Ollama In-App (NEW)
- [ ] OllamaService for embedded server management
- [ ] Model download/delete/list operations
- [ ] Model download progress UI
- [ ] Support for Gemma 4, Llama 3, Codellama

### Phase 6 - PC CLI Integration (NEW)
- [ ] PC CLI Tool (Python/FastAPI)
- [ ] PCConnectionManager in Android app
- [ ] Remote file editing via phone
- [ ] Git operations from phone

## 7. Package Structure

```
com.pocketdev/
├── data/
│   ├── di/                    # Hilt modules
│   ├── local/                 # DataStore, Room
│   │   └── ollama/           # Ollama binary manager
│   ├── remote/
│   │   ├── api/              # Retrofit interfaces
│   │   ├── interceptor/      # DynamicHostInterceptor
│   │   └── ollama/           # Ollama API client
│   └── repository/           # Repository implementations
├── domain/
│   ├── model/                # Domain models
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic
└── ui/
    ├── components/           # Reusable Compose components
    ├── screens/              # Screen composables
    │   ├── chat/
    │   ├── settings/
    │   ├── repos/
    │   ├── ollama/           # NEW: Model management
    │   └── pc/               # NEW: PC connection
    ├── theme/                # Material 3 Theme
    └── navigation/           # Navigation setup

# PC CLI Tool (separate module)
cli/
├── pocketdev_cli/           # Python package
│   ├── __main__.py
│   ├── server.py            # FastAPI server
│   ├── file_ops.py          # File operations
│   ├── git_ops.py           # Git operations
│   └── shell.py             # Shell command execution
├── pyproject.toml
└── README.md
```

## 8. Security Considerations

1. **API Key Storage:** EncryptedSharedPreferences wrapper for DataStore
2. **OAuth:** Chrome Custom Tabs (not WebView) for GitHub login
3. **PC CLI Auth:** API key required for all endpoints
4. **Ollama Localhost:** Document 10.0.2.2 mapping for Android emulator
5. **Remote Tunnels:** Use ngrok with auth for remote PC access