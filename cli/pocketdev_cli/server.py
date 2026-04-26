"""
PocketDev CLI - FastAPI Server

File operations, Git operations, and shell command execution
for remote control from Android app.
"""
import os
import sys
import subprocess
import base64
import platform
from pathlib import Path
from typing import Optional

from fastapi import FastAPI, HTTPException, Header, Depends
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel
import uvicorn

# Configuration
DEFAULT_PORT = 8080
DEFAULT_HOST = "0.0.0.0"

# Models
class FileReadRequest(BaseModel):
    path: str

class FileWriteRequest(BaseModel):
    path: str
    content: str
    base64_encode: bool = False

class FileResponse(BaseModel):
    success: bool
    content: Optional[str] = None
    error: Optional[str] = None

class GitCommitRequest(BaseModel):
    message: str
    files: list[str] = []
    cwd: Optional[str] = None

class GitCommitResponse(BaseModel):
    success: bool
    sha: Optional[str] = None
    message: Optional[str] = None
    error: Optional[str] = None

class ShellRequest(BaseModel):
    command: str
    cwd: Optional[str] = None
    timeout: int = 30

class ShellResponse(BaseModel):
    success: bool
    stdout: Optional[str] = None
    stderr: Optional[str] = None
    exit_code: Optional[int] = None
    error: Optional[str] = None

class SystemInfo(BaseModel):
    hostname: str
    platform: str
    python_version: str
    git_version: Optional[str]
    working_directory: str
    ollama_url: Optional[str] = None

# FastAPI App
app = FastAPI(
    title="PocketDev CLI",
    description="PC Control Server for PocketDev Android App",
    version="1.0.0"
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Simple API key auth (optional)
API_KEY: Optional[str] = None

def verify_api_key(x_authorization: Optional[str] = Header(None)):
    if API_KEY:
        if not x_authorization:
            raise HTTPException(status_code=401, detail="API key required")
        if x_authorization != f"Bearer {API_KEY}":
            raise HTTPException(status_code=401, detail="Invalid API key")
    return True

@app.get("/api/system/info", response_model=SystemInfo)
async def get_system_info(_: bool = Depends(verify_api_key)):
    """Get system information"""
    hostname = platform.node()
    system = platform.system()
    python_version = platform.python_version()

    # Get git version
    git_version = None
    try:
        result = subprocess.run(
            ["git", "--version"],
            capture_output=True,
            text=True,
            timeout=5
        )
        git_version = result.stdout.strip()
    except Exception:
        pass

    # Get Ollama URL (if available)
    ollama_url = os.environ.get("OLLAMA_BASE_URL", "http://localhost:11434")

    return SystemInfo(
        hostname=hostname,
        platform=system,
        python_version=python_version,
        git_version=git_version,
        working_directory=os.getcwd(),
        ollama_url=ollama_url
    )

@app.post("/api/files/read", response_model=FileResponse)
async def read_file(request: FileReadRequest, _: bool = Depends(verify_api_key)):
    """Read file contents"""
    try:
        path = Path(request.path).expanduser().resolve()

        if not path.exists():
            return FileResponse(success=False, error=f"File not found: {request.path}")

        if not path.is_file():
            return FileResponse(success=False, error=f"Not a file: {request.path}")

        content = path.read_text(encoding="utf-8")
        return FileResponse(success=True, content=content)

    except Exception as e:
        return FileResponse(success=False, error=str(e))

@app.post("/api/files/write", response_model=FileResponse)
async def write_file(request: FileWriteRequest, _: bool = Depends(verify_api_key)):
    """Write file contents"""
    try:
        path = Path(request.path).expanduser().resolve()

        # Security: prevent path traversal
        if ".." in request.path:
            return FileResponse(success=False, error="Path traversal not allowed")

        # Create parent directories if needed
        path.parent.mkdir(parents=True, exist_ok=True)

        content = request.content
        if request.base64_encode:
            content = base64.b64decode(content).decode("utf-8")

        path.write_text(content, encoding="utf-8")
        return FileResponse(success=True)

    except Exception as e:
        return FileResponse(success=False, error=str(e))

@app.post("/api/git/commit", response_model=GitCommitResponse)
async def git_commit(request: GitCommitRequest, _: bool = Depends(verify_api_key)):
    """Perform git commit"""
    try:
        cwd = request.cwd or os.getcwd()

        # Stage files
        if request.files:
            for file in request.files:
                result = subprocess.run(
                    ["git", "add", file],
                    cwd=cwd,
                    capture_output=True,
                    text=True,
                    timeout=10
                )
                if result.returncode != 0:
                    return GitCommitResponse(
                        success=False,
                        error=f"Failed to stage {file}: {result.stderr}"
                    )
        else:
            # Stage all changes
            result = subprocess.run(
                ["git", "add", "-A"],
                cwd=cwd,
                capture_output=True,
                text=True,
                timeout=10
            )
            if result.returncode != 0:
                return GitCommitResponse(
                    success=False,
                    error=f"Failed to stage files: {result.stderr}"
                )

        # Check if there are changes to commit
        result = subprocess.run(
            ["git", "status", "--porcelain"],
            cwd=cwd,
            capture_output=True,
            text=True,
            timeout=10
        )
        if not result.stdout.strip():
            return GitCommitResponse(
                success=False,
                error="No changes to commit"
            )

        # Commit
        result = subprocess.run(
            ["git", "commit", "-m", request.message],
            cwd=cwd,
            capture_output=True,
            text=True,
            timeout=30
        )

        if result.returncode != 0:
            return GitCommitResponse(
                success=False,
                error=f"Commit failed: {result.stderr}"
            )

        # Get commit SHA
        result = subprocess.run(
            ["git", "rev-parse", "HEAD"],
            cwd=cwd,
            capture_output=True,
            text=True,
            timeout=10
        )

        sha = result.stdout.strip()[:8] if result.returncode == 0 else None

        return GitCommitResponse(
            success=True,
            sha=sha,
            message=result.stderr or "Committed successfully"
        )

    except Exception as e:
        return GitCommitResponse(success=False, error=str(e))

@app.post("/api/shell/execute", response_model=ShellResponse)
async def execute_shell(request: ShellRequest, _: bool = Depends(verify_api_key)):
    """Execute shell command"""
    try:
        result = subprocess.run(
            request.command,
            shell=True,
            cwd=request.cwd or os.getcwd(),
            capture_output=True,
            text=True,
            timeout=request.timeout
        )

        return ShellResponse(
            success=result.returncode == 0,
            stdout=result.stdout,
            stderr=result.stderr,
            exit_code=result.returncode
        )

    except subprocess.TimeoutExpired:
        return ShellResponse(
            success=False,
            error=f"Command timed out after {request.timeout}s",
            exit_code=-1
        )
    except Exception as e:
        return ShellResponse(success=False, error=str(e), exit_code=-1)

@app.post("/api/ollama/chat")
async def ollama_chat(request: dict, _: bool = Depends(verify_api_key)):
    """Proxy chat requests to local Ollama"""
    try:
        import requests

        ollama_url = os.environ.get("OLLAMA_BASE_URL", "http://localhost:11434")
        response = requests.post(
            f"{ollama_url}/v1/chat/completions",
            json=request,
            timeout=120
        )
        response.raise_for_status()
        return response.json()

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "ok", "service": "pocketdev-cli"}

def run_server(host: str = DEFAULT_HOST, port: int = DEFAULT_PORT, api_key: Optional[str] = None):
    """Run the server"""
    global API_KEY
    API_KEY = api_key

    print(f"""
╔═══════════════════════════════════════════════════════════╗
║                    PocketDev CLI Server                    ║
╠═══════════════════════════════════════════════════════════╣
║  Server running at: http://{host}:{port}                       ║
║  API Key: {'Required' if api_key else 'Not configured'}                                     ║
╠═══════════════════════════════════════════════════════════╣
║  Endpoints:                                               ║
║    GET  /api/system/info  - System information            ║
║    POST /api/files/read   - Read file                     ║
║    POST /api/files/write  - Write file                    ║
║    POST /api/git/commit   - Git commit                    ║
║    POST /api/shell/execute - Execute shell command        ║
║    POST /api/ollama/chat  - Proxy to Ollama               ║
╚═══════════════════════════════════════════════════════════╝
    """)

    uvicorn.run(app, host=host, port=port, log_level="info")

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="PocketDev CLI Server")
    parser.add_argument("--host", default=DEFAULT_HOST, help="Host to bind to")
    parser.add_argument("--port", type=int, default=DEFAULT_PORT, help="Port to bind to")
    parser.add_argument("--api-key", help="API key for authentication")
    parser.add_argument("--ollama-url", help="Ollama base URL (env: OLLAMA_BASE_URL)")

    args = parser.parse_args()

    if args.ollama_url:
        os.environ["OLLAMA_BASE_URL"] = args.ollama_url

    run_server(host=args.host, port=args.port, api_key=args.api_key)
