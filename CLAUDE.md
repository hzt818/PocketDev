# PocketDev - Android AI Programming Assistant

## Project Overview

PocketDev is an Android application that enables AI-powered mobile coding assistance with:
- **Cloud AI**: DeepSeek, OpenAI, Claude, Gemini, MiniMax, Kimi, GLM
- **Local AI**: Ollama with downloadable models (Gemma 4, Llama 3, etc.)
- **Remote Control**: PC CLI for real file editing and git operations
- **Agent System**: Autonomous coding agents for large-scale development

## Tech Stack

| Category | Technology |
|---------|------------|
| **Platform** | Android 8.0+ (API 26+) |
| **Language** | Kotlin (Strict Mode) |
| **UI** | Jetpack Compose + Material 3 + DynamicColors |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt |
| **Network** | Retrofit + OkHttp + DynamicHostInterceptor |
| **Async** | Coroutines + Flow |
| **Storage** | DataStore (Preferences) + Room |
| **JSON** | Kotlinx Serialization |
| **Agent** | Custom Agent Framework |
| **PC Tool** | Python + FastAPI |

## Package Structure

```
com.pocketdev/
├── agent/                          # Agent System (NEW)
│   ├── core/                      # Agent, AgentExecutor, AgentLoopOperator
│   ├── protocol/                  # AgentRequest, AgentResponse, Finding types
│   └── agents/                    # Specialized agents
├── data/                          # Data Layer
│   ├── di/                        # Hilt Modules
│   ├── local/                     # DataStore, GitHubAuthManager
│   ├── remote/
│   │   ├── api/                  # Retrofit interfaces
│   │   └── interceptor/          # DynamicHostInterceptor
│   └── repository/                # Repository implementations
├── domain/                        # Domain Layer
│   ├── model/                     # Domain models
│   ├── repository/               # Repository interfaces
│   └── usecase/                  # Business logic
└── ui/                           # Presentation Layer
    ├── components/                 # Reusable Compose
    ├── screens/                   # Screen composables
    ├── navigation/                # NavHost
    └── theme/                     # Material 3 Theme
```

---

## Agent System Architecture

### Core Components

| Component | File | Purpose |
|-----------|------|---------|
| **Agent Protocol** | `agent/protocol/AgentProtocol.kt` | Defines request/response types |
| **Agent Interface** | `agent/core/Agent.kt` | Base interface for all agents |
| **AgentExecutor** | `agent/core/AgentExecutor.kt` | Manages execution, retries, parallelism |
| **AgentLoopOperator** | `agent/core/AgentLoopOperator.kt` | Autonomous Think-Act-Observe-Decide loop |
| **AgentService** | `agent/AgentService.kt` | Main entry point for app integration |

### Available Agents

| Agent | Type | Capabilities |
|-------|------|--------------|
| **PlannerAgent** | `PLANNER` | Creates phased implementation plans |
| **CodeReviewerAgent** | `CODE_REVIEWER` | Static analysis, bug detection, quality metrics |
| **TDDGuideAgent** | `TDG_GUIDE` | Red-Green-Refactor workflow guidance |
| **SecurityReviewerAgent** | `SECURITY_REVIEWER` | OWASP Top 10, vulnerability detection |
| **BuildErrorResolverAgent** | `BUILD_ERROR_RESOLVER` | Compilation error analysis and fixes |
| **RefactorCleanerAgent** | `REFACTOR_CLEANER` | Dead code detection, cleanup |
| **DocUpdaterAgent** | `DOC_UPDATER` | Documentation generation/updates |
| **E2ERunnerAgent** | `E2E_RUNNER` | End-to-end test execution |

### Agent Protocol

```kotlin
// Request types
sealed class AgentRequest {
    data class CodeAnalysisRequest(...)
    data class ImplementationRequest(...)
    data class SecurityReviewRequest(...)
    data class TDDGuideRequest(...)
    data class BuildFixRequest(...)
}

// Response types
sealed class AgentResponse {
    data class CodeAnalysisResponse(...)
    data class ImplementationResponse(...)
    data class SecurityReviewResponse(...)
    data class BuildFixResponse(...)
}

// Common findings structure
data class Finding(
    val type: FindingType,      // BUG, VULNERABILITY, CODE_SMELL, etc.
    val severity: Severity,       // CRITICAL, HIGH, MEDIUM, LOW, INFO
    val file: String,
    val line: Int?,
    val message: String,
    val suggestion: String?
)
```

### Agent Loop (Think-Act-Observe-Decide)

```kotlin
// Start autonomous loop for complex tasks
val progress = agentService.startAutonomousLoop(
    goal = "Implement user authentication",
    context = AgentContext(projectPath = "/path/to/project")
)

// Progress updates
progress.collect { when(it) {
    is LoopProgress.Thinking -> showThinkingIndicator()
    is LoopProgress.Acting -> showAction(it.description)
    is LoopProgress.Observing -> displayFindings(it.response)
    is LoopProgress.Completed -> showSuccess(it.summary)
    is LoopProgress.MaxIterationsReached -> showWarning()
}}
```

### Task Graph Execution

```kotlin
// Build dependency graph for complex workflows
val tasks = taskGraph(
    CodeAnalysisRequest(id = "1", context = ctx)
).addTask(
    SecurityReviewRequest(id = "2", context = ctx),
    dependencies = listOf("1")  // Wait for analysis first
).addTask(
    ImplementationRequest(id = "3", requirements = "..."),
    dependencies = listOf("2")  // Wait for security review
).build()

val results = agentService.executeTaskGraph(tasks)
```

---

## Key Architecture Decisions

### 1. Clean Architecture Layers

**Domain Layer must NOT depend on:**
- Any Android framework classes
- Retrofit, OkHttp, or other network libraries
- DataStore, Room, or other storage

**Data Layer implements interfaces defined in Domain Layer**

### 2. Dynamic Host Interceptor

The `DynamicHostInterceptor` enables runtime AI provider switching:
- Reads baseUrl/apiKey from DataStore via cache
- Replaces request URL scheme/host/port
- Injects Authorization header
- Supports multiple API formats (OpenAI, Anthropic, Gemini, Ollama)

### 3. Agent Protocol Design

Agents communicate via typed requests/responses:
- Structured findings with severity levels
- Context propagation across agents
- Task dependency graphs for complex workflows
- Async execution with timeout and retry

---

## Coding Conventions

### Immutability (CRITICAL)

**ALWAYS create new objects, NEVER mutate existing ones:**
```kotlin
// WRONG
state.messages.add(newMessage)

// CORRECT
state.copy(messages = state.messages + newMessage)
```

### Agent Implementation

```kotlin
class MyAgent : Agent {
    override val type = AgentType.MY_AGENT
    override val name = "My Agent"
    override val description = "What it does"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is MyRequest) { "Invalid request type" }

        val findings = analyze(request.context)

        return MyResponse(
            requestId = request.id,
            success = true,
            findings = findings
        )
    }
}
```

### Error Handling

- Use `Result<T>` for operations that can fail
- Handle errors at every layer
- Provide user-friendly messages in UI
- Log detailed context server-side

---

## Development Workflow

### 1. Research First
- Search GitHub for existing implementations
- Check library docs (use Context7 MCP)
- Search package registries

### 2. Plan with Agent
- Use **planner** agent for complex features
- Generate phased implementation plans
- Assess complexity and risks

### 3. TDD Approach
- Use **tdd-guide** agent for workflow
- Write tests first (RED)
- Implement to pass tests (GREEN)
- Target 80%+ coverage

### 4. Code Review
- Use **code-reviewer** agent after writing code
- Use **security-reviewer** agent before commit
- Address CRITICAL and HIGH issues

### 5. Build Error Resolution
- Use **build-error-resolver** agent for fixes
- Pattern-based error recognition
- Automatic fix generation

---

## Build & Run

### Android App
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### PC CLI
```bash
pip install pocketdev-cli
pocketdev-cli serve --port 8080 --api-key your-key
```

---

## Security Checklist

- [ ] API keys stored in DataStore (not hardcoded)
- [ ] OAuth uses Chrome Custom Tabs (not WebView)
- [ ] PC CLI requires API key authentication
- [ ] Path traversal prevented in file operations
- [ ] Agent security review for new code

---

## Testing Requirements

- Unit tests for ViewModels and UseCases
- Unit tests for Agents
- Integration tests for Repository layer
- Minimum 80% coverage for business logic

---

## Useful Commands

```bash
./gradlew ktlintCheck
./gradlew test
./gradlew assembleRelease
```
