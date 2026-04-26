# PocketDev CLI

PC Control Server for the PocketDev Android App. Enables remote file editing, git operations, and shell command execution from your phone.

## Installation

### From PyPI (Recommended)
```bash
pip install pocketdev-cli
pocketdev-cli serve
```

### From Source
```bash
git clone https://github.com/pocketdev/pocketdev-cli.git
cd pocketdev-cli
pip install -e .
```

## Quick Start

```bash
# Start server with default settings
pocketdev-cli serve

# Start with custom port and API key
pocketdev-cli serve --port 9000 --api-key my-secret-key

# Specify Ollama URL for AI proxy
pocketdev-cli serve --ollama-url http://localhost:11434
```

## Features

- **File Operations**: Read/write files on your PC from the Android app
- **Git Integration**: Commit changes without switching to your computer
- **Shell Commands**: Execute terminal commands remotely
- **Ollama Proxy**: Route AI requests through your local Ollama server
- **API Key Security**: Optional authentication for public networks

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/system/info` | GET | System information |
| `/api/files/read` | POST | Read file contents |
| `/api/files/write` | POST | Write file contents |
| `/api/git/commit` | POST | Git commit |
| `/api/shell/execute` | POST | Execute shell command |
| `/api/ollama/chat` | POST | Proxy to Ollama |
| `/health` | GET | Health check |

## Android App Setup

1. Open PocketDev Android app
2. Go to Settings → PC Connection
3. Add new connection:
   - **Name**: Your PC name
   - **Host**: PC IP address (e.g., `192.168.1.100`)
   - **Port**: Server port (default: 8080)
   - **API Key**: Your configured API key (if any)

## Security Notes

- **Local Network**: Server is designed for use within your local network
- **API Key**: Use `--api-key` when exposing to untrusted networks
- **Firewall**: Ensure the port is open on your PC's firewall
- **ngrok/tunnel**: For remote access, use a tunnel service with authentication

## Use Cases

### Local Development
1. Start `pocketdev-cli serve` on your development PC
2. Connect Android app to your PC's local IP
3. Use AI chat to generate code
4. Commit directly from your phone

### Remote Access
```bash
# Install ngrok
ngrok http 8080

# Use the ngrok URL in Android app
# Example: https://abc123.ngrok.io
```

## License

MIT License
