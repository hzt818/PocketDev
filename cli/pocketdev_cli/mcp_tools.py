"""
PocketDev MCP Tools - Python CLI wrappers for MCP tools

This module provides Python implementations of the MCP tools
that can be invoked via subprocess from the Node.js MCP server
or directly via the Python CLI.
"""
import json
import os
import subprocess
from dataclasses import dataclass
from pathlib import Path
from typing import Any


@dataclass
class ToolResult:
    success: bool
    data: Any
    error: str | None = None

    def to_json(self) -> str:
        return json.dumps({
            "success": self.success,
            "data": self.data,
            "error": self.error,
        })


def validate_path(path: str) -> ToolResult:
    """Validate path to prevent directory traversal."""
    normalized = Path(path).resolve()
    if ".." in Path(path).parts:
        return ToolResult(
            success=False,
            data=None,
            error="Path traversal detected"
        )
    return ToolResult(success=True, data={"path": str(normalized)})


def read_file(path: str, encoding: str = "utf-8") -> ToolResult:
    """Read file contents."""
    validation = validate_path(path)
    if not validation.success:
        return validation

    try:
        file_path = Path(path)
        if not file_path.exists():
            return ToolResult(success=False, data=None, error="File not found")
        if not file_path.is_file():
            return ToolResult(success=False, data=None, error="Not a file")

        content = file_path.read_text(encoding=encoding)
        return ToolResult(
            success=True,
            data={
                "path": str(file_path),
                "content": content,
                "size": len(content),
            }
        )
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def write_file(path: str, content: str, create_dirs: bool = False) -> ToolResult:
    """Write content to file."""
    validation = validate_path(path)
    if not validation.success:
        return validation

    try:
        file_path = Path(path)
        if create_dirs:
            file_path.parent.mkdir(parents=True, exist_ok=True)

        file_path.write_text(content, encoding="utf-8")
        return ToolResult(
            success=True,
            data={
                "path": str(file_path),
                "bytes_written": len(content),
            }
        )
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def list_directory(path: str, include_hidden: bool = False) -> ToolResult:
    """List directory contents."""
    validation = validate_path(path)
    if not validation.success:
        return validation

    try:
        dir_path = Path(path)
        if not dir_path.exists():
            return ToolResult(success=False, data=None, error="Directory not found")
        if not dir_path.is_dir():
            return ToolResult(success=False, data=None, error="Not a directory")

        entries = []
        for item in dir_path.iterdir():
            if not include_hidden and item.name.startswith("."):
                continue
            entries.append({
                "name": item.name,
                "path": str(item),
                "is_directory": item.is_dir(),
                "is_file": item.is_file(),
                "size": item.stat().st_size if item.is_file() else 0,
            })

        entries.sort(key=lambda x: (not x["is_directory"], x["name"].lower()))
        return ToolResult(
            success=True,
            data={
                "path": str(dir_path),
                "entries": entries,
                "count": len(entries),
            }
        )
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def search_files(pattern: str, root: str = ".", max_results: int = 100) -> ToolResult:
    """Search for files matching glob pattern."""
    validation = validate_path(root)
    if not validation.success:
        return validation

    try:
        root_path = Path(root).resolve()
        matches = list(root_path.glob(pattern))[:max_results]
        return ToolResult(
            success=True,
            data={
                "pattern": pattern,
                "root": str(root_path),
                "matches": [str(m) for m in matches],
                "count": len(matches),
            }
        )
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def git_status(repo_path: str = ".") -> ToolResult:
    """Get git repository status."""
    try:
        repo = Path(repo_path).resolve()
        result = subprocess.run(
            ["git", "status", "--porcelain"],
            cwd=str(repo),
            capture_output=True,
            text=True,
            check=True,
        )
        branch_result = subprocess.run(
            ["git", "branch", "--show-current"],
            cwd=str(repo),
            capture_output=True,
            text=True,
            check=True,
        )
        return ToolResult(
            success=True,
            data={
                "path": str(repo),
                "branch": branch_result.stdout.strip(),
                "changes": [l for l in result.stdout.strip().split("\n") if l],
            }
        )
    except subprocess.CalledProcessError as e:
        return ToolResult(success=False, data=None, error=e.stderr or str(e))
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def git_commit(message: str, repo_path: str = ".", add_all: bool = True) -> ToolResult:
    """Create git commit."""
    try:
        repo = Path(repo_path).resolve()
        if add_all:
            subprocess.run(["git", "add", "-A"], cwd=str(repo), check=True)
        result = subprocess.run(
            ["git", "commit", "-m", message],
            cwd=str(repo),
            capture_output=True,
            text=True,
            check=True,
        )
        return ToolResult(
            success=True,
            data={
                "message": message,
                "output": result.stdout.strip(),
            }
        )
    except subprocess.CalledProcessError as e:
        return ToolResult(success=False, data=None, error=e.stderr or str(e))
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def git_log(repo_path: str = ".", limit: int = 20) -> ToolResult:
    """Get git commit history."""
    try:
        repo = Path(repo_path).resolve()
        result = subprocess.run(
            ["git", "log", f"--oneline", f"-{limit}"],
            cwd=str(repo),
            capture_output=True,
            text=True,
            check=True,
        )
        return ToolResult(
            success=True,
            data={
                "path": str(repo),
                "commits": [c for c in result.stdout.strip().split("\n") if c],
            }
        )
    except subprocess.CalledProcessError as e:
        return ToolResult(success=False, data=None, error=e.stderr or str(e))
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def execute_shell(command: str, cwd: str = ".", timeout: int = 30) -> ToolResult:
    """Execute shell command."""
    validation = validate_path(cwd)
    if not validation.success:
        return validation

    try:
        result = subprocess.run(
            command,
            shell=True,
            cwd=str(Path(cwd).resolve()),
            capture_output=True,
            text=True,
            timeout=timeout,
        )
        return ToolResult(
            success=True,
            data={
                "command": command,
                "cwd": str(Path(cwd).resolve()),
                "output": result.stdout.strip(),
                "stderr": result.stderr.strip(),
                "return_code": result.returncode,
            }
        )
    except subprocess.TimeoutExpired:
        return ToolResult(success=False, data=None, error=f"Command timed out after {timeout}s")
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def system_info() -> ToolResult:
    """Get system information."""
    import platform
    import os

    try:
        info = {
            "hostname": platform.node(),
            "platform": platform.system(),
            "platform_release": platform.release(),
            "platform_version": platform.version(),
            "arch": platform.machine(),
            "processor": platform.processor(),
            "cpu_count": os.cpu_count(),
            "total_memory": getattr(os, "totalmem", lambda: 0)(),
            "free_memory": getattr(os, "freemem", lambda: 0)(),
            "uptime": getattr(os, "getuptime", lambda: 0)(),
        }
        return ToolResult(success=True, data=info)
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


def ai_chat(message: str, model: str = "codellama", system: str = None) -> ToolResult:
    """Send chat to AI via Ollama."""
    try:
        import requests
        host = os.environ.get("POCKETDEV_HOST", "localhost")
        port = os.environ.get("POCKETDEV_PORT", "11434")

        payload = {
            "model": model,
            "messages": [
                {"role": "user", "content": message}
            ]
        }
        if system:
            payload["messages"].insert(0, {"role": "system", "content": system})

        response = requests.post(
            f"http://{host}:{port}/api/chat",
            json=payload,
            timeout=60,
        )
        response.raise_for_status()
        result = response.json()
        return ToolResult(
            success=True,
            data={
                "model": model,
                "response": result.get("message", {}).get("content", ""),
            }
        )
    except ImportError:
        return ToolResult(
            success=False,
            data=None,
            error="requests library not available"
        )
    except Exception as e:
        return ToolResult(success=False, data=None, error=str(e))


# Tool name to function mapping
TOOL_FUNCTIONS = {
    "pocketdev_read_file": lambda args: read_file(args.get("path", ""), args.get("encoding", "utf-8")),
    "pocketdev_write_file": lambda args: write_file(args.get("path", ""), args.get("content", ""), args.get("createDirectories", False)),
    "pocketdev_list_directory": lambda args: list_directory(args.get("path", ""), args.get("includeHidden", False)),
    "pocketdev_search_files": lambda args: search_files(args.get("pattern", ""), args.get("root", "."), args.get("maxResults", 100)),
    "pocketdev_git_status": lambda args: git_status(args.get("path", ".")),
    "pocketdev_git_commit": lambda args: git_commit(args.get("message", ""), args.get("path", "."), args.get("addAll", True)),
    "pocketdev_git_log": lambda args: git_log(args.get("path", "."), args.get("limit", 20)),
    "pocketdev_execute_shell": lambda args: execute_shell(args.get("command", ""), args.get("cwd", "."), args.get("timeout", 30)),
    "pocketdev_system_info": lambda _: system_info(),
    "pocketdev_ai_chat": lambda args: ai_chat(args.get("message", ""), args.get("model", "codellama"), args.get("system")),
}


def invoke_tool(tool_name: str, arguments: dict) -> str:
    """Invoke a tool by name with arguments."""
    if tool_name not in TOOL_FUNCTIONS:
        return ToolResult(
            success=False,
            data=None,
            error=f"Unknown tool: {tool_name}"
        ).to_json()

    result = TOOL_FUNCTIONS[tool_name](arguments)
    return result.to_json()


if __name__ == "__main__":
    import sys
    if len(sys.argv) < 3:
        print("Usage: python -m pocketdev_cli.mcp_tools <tool_name> <arguments_json>")
        sys.exit(1)

    tool_name = sys.argv[1]
    args = json.loads(sys.argv[2])
    print(invoke_tool(tool_name, args))
