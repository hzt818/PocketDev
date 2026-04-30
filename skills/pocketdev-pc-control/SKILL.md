---
name: pocketdev-pc-control
description: PocketDev PC Control - Remote file operations, git workflows, and AI chat via PC CLI
origin: pocketdev
tags:
  - android
  - pocketdev
  - remote-control
  - file-operations
  - git
  - ai
---

# PocketDev PC Control

Skills for integrating Claude Code with PocketDev Android app's PC CLI for remote development workflows.

## When to Activate

- User mentions "PocketDev", "PC control", "remote development"
- Working with Android project files on remote PC
- Performing git operations from Android
- Running AI models via Ollama on PC
- User invokes `/pocketdev-pc-control` or `/pc`

## Prerequisites

1. **PC CLI Server running:**
   ```bash
   pocketdev-cli serve --port 8080 --api-key your-key
   ```

2. **MCP Server running:**
   ```bash
   cd e:/pocketdev/cli/mcp-server
   npm install
   npm run dev
   ```

3. **Network connectivity** between Android and PC

## Available Tools

### File Operations

| Tool | Description | Usage |
|------|-------------|-------|
| `pocketdev_read_file` | Read file contents | `Read path:/path/to/file.kt` |
| `pocketdev_write_file` | Write file contents | `Write path:content:` |
| `pocketdev_list_directory` | List directory | `List path:/project` |
| `pocketdev_search_files` | Search files by pattern | `Search pattern:**/*.kt` |

### Git Operations

| Tool | Description | Usage |
|------|-------------|-------|
| `pocketdev_git_status` | Check git status | `GitStatus path:` |
| `pocketdev_git_commit` | Commit changes | `GitCommit message:` |
| `pocketdev_git_log` | View history | `GitLog limit:20` |

### AI Chat

| Tool | Description | Usage |
|------|-------------|-------|
| `pocketdev_ai_chat` | Chat with AI | `AIChat message: model:deepseek` |

### System

| Tool | Description | Usage |
|------|-------------|-------|
| `pocketdev_execute_shell` | Run shell command | `Shell command:ls -la` |
| `pocketdev_system_info` | Get system info | `SystemInfo` |

## Usage Examples

### Read a file from PC
```
/pc read /home/user/project/src/main/java/com/example/App.kt
```

### Write configuration
```
/pc write ~/.gradle/gradle.properties "org.gradle.jvmargs=-Xmx4g"
```

### Git commit changes
```
/pc commit "feat: add new feature"
```

### Chat with local AI
```
/pc chat "Explain this code" --model codellama
```

### Search for files
```
/pc search **/*.kt --root /project
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `POCKETDEV_HOST` | localhost | PC hostname or IP |
| `POCKETDEV_PORT` | 8080 | Server port |
| `POCKETDEV_API_KEY` | - | API key (required) |
| `POCKETDEV_CLI` | python | Python executable path |

## Security

- All endpoints require API key authentication
- Path traversal (`..`) is blocked
- Dangerous shell commands are blocked by hooks
- Shell commands have timeout limits (default 30s)
- All operations are logged

## See Also

- Skill: `android-clean-architecture`
- Skill: `kotlin-coroutines-flows`
- PC CLI documentation in `cli/README.md`
- MCP server in `cli/mcp-server/`
