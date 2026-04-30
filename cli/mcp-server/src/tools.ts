import { Tool, CallToolResult, TextContent } from '@modelcontextprotocol/sdk/types.js';
import { execSync, spawn } from 'child_process';
import { readFile, writeFile, readdir, stat } from 'fs/promises';
import { join, resolve, normalize } from 'path';

const POCKETDEV_CLI = process.env.POCKETDEV_CLI || 'python';
const POCKETDEV_PORT = process.env.POCKETDEV_PORT || '8080';
const POCKETDEV_HOST = process.env.POCKETDEV_HOST || 'localhost';

export interface PocketDevConfig {
  apiKey?: string;
  host?: string;
  port?: string;
}

let config: PocketDevConfig = {
  host: POCKETDEV_HOST,
  port: POCKETDEV_PORT,
  apiKey: process.env.POCKETDEV_API_KEY,
};

export function setConfig(newConfig: Partial<PocketDevConfig>) {
  config = { ...config, ...newConfig };
}

export const TOOLS: Tool[] = [
  {
    name: 'pocketdev_read_file',
    description: 'Read contents of a file from the PC. Use for viewing source code, configurations, or any text file.',
    inputSchema: {
      type: 'object',
      properties: {
        path: {
          type: 'string',
          description: 'Absolute path to the file to read',
        },
        encoding: {
          type: 'string',
          description: 'File encoding (default: utf-8)',
          default: 'utf-8',
        },
      },
      required: ['path'],
    },
  },
  {
    name: 'pocketdev_write_file',
    description: 'Write content to a file on the PC. Creates the file if it does not exist, overwrites if it does.',
    inputSchema: {
      type: 'object',
      properties: {
        path: {
          type: 'string',
          description: 'Absolute path to the file to write',
        },
        content: {
          type: 'string',
          description: 'Content to write to the file',
        },
        createDirectories: {
          type: 'boolean',
          description: 'Create parent directories if they do not exist',
          default: false,
        },
      },
      required: ['path', 'content'],
    },
  },
  {
    name: 'pocketdev_list_directory',
    description: 'List contents of a directory with file type information.',
    inputSchema: {
      type: 'object',
      properties: {
        path: {
          type: 'string',
          description: 'Absolute path to the directory to list',
        },
        includeHidden: {
          type: 'boolean',
          description: 'Include hidden files (starting with .)',
          default: false,
        },
      },
      required: ['path'],
    },
  },
  {
    name: 'pocketdev_search_files',
    description: 'Search for files matching a glob pattern.',
    inputSchema: {
      type: 'object',
      properties: {
        pattern: {
          type: 'string',
          description: 'Glob pattern to match (e.g., **/*.kt, **/*.java)',
        },
        root: {
          type: 'string',
          description: 'Root directory to search from',
        },
        maxResults: {
          type: 'number',
          description: 'Maximum number of results to return',
          default: 100,
        },
      },
      required: ['pattern'],
    },
  },
  {
    name: 'pocketdev_git_status',
    description: 'Get the current git repository status.',
    inputSchema: {
      type: 'object',
      properties: {
        path: {
          type: 'string',
          description: 'Path to the git repository (defaults to current directory)',
        },
      },
    },
  },
  {
    name: 'pocketdev_git_commit',
    description: 'Create a git commit with the given message.',
    inputSchema: {
      type: 'object',
      properties: {
        message: {
          type: 'string',
          description: 'Commit message',
        },
        path: {
          type: 'string',
          description: 'Path to the git repository',
        },
        addAll: {
          type: 'boolean',
          description: 'Stage all modified files before committing',
          default: true,
        },
      },
      required: ['message'],
    },
  },
  {
    name: 'pocketdev_git_log',
    description: 'Get recent git commit history.',
    inputSchema: {
      type: 'object',
      properties: {
        path: {
          type: 'string',
          description: 'Path to the git repository',
        },
        limit: {
          type: 'number',
          description: 'Number of commits to return',
          default: 20,
        },
      },
    },
  },
  {
    name: 'pocketdev_execute_shell',
    description: 'Execute a shell command on the PC. Use with caution - this has full system access.',
    inputSchema: {
      type: 'object',
      properties: {
        command: {
          type: 'string',
          description: 'Shell command to execute',
        },
        cwd: {
          type: 'string',
          description: 'Working directory for the command',
        },
        timeout: {
          type: 'number',
          description: 'Timeout in seconds',
          default: 30,
        },
      },
      required: ['command'],
    },
  },
  {
    name: 'pocketdev_system_info',
    description: 'Get system information from the PC.',
    inputSchema: {
      type: 'object',
      properties: {},
    },
  },
  {
    name: 'pocketdev_ai_chat',
    description: 'Send a chat message to AI (Ollama or configured provider).',
    inputSchema: {
      type: 'object',
      properties: {
        message: {
          type: 'string',
          description: 'Message to send to the AI',
        },
        model: {
          type: 'string',
          description: 'AI model to use (e.g., codellama, deepseek-coder)',
        },
        system: {
          type: 'string',
          description: 'System prompt to use',
        },
      },
      required: ['message'],
    },
  },
];

function validatePath(filePath: string): { valid: boolean; error?: string } {
  const normalized = normalize(filePath);
  if (normalized.includes('..')) {
    return { valid: false, error: 'Path traversal detected' };
  }
  return { valid: true };
}

export async function callTool(name: string, arguments_: Record<string, unknown>): Promise<CallToolResult> {
  try {
    switch (name) {
      case 'pocketdev_read_file': {
        const path = arguments_.path as string;
        const validation = validatePath(path);
        if (!validation.valid) {
          return {
            content: [{ type: 'text', text: JSON.stringify({ error: validation.error }) }],
            isError: true,
          };
        }
        const content = await readFile(path, { encoding: arguments_.encoding as string || 'utf-8' });
        return {
          content: [{ type: 'text', text: JSON.stringify({ path, content, size: content.length }) }],
        };
      }

      case 'pocketdev_write_file': {
        const path = arguments_.path as string;
        const content = arguments_.content as string;
        const validation = validatePath(path);
        if (!validation.valid) {
          return {
            content: [{ type: 'text', text: JSON.stringify({ error: validation.error }) }],
            isError: true,
          };
        }
        await writeFile(path, content);
        return {
          content: [{ type: 'text', text: JSON.stringify({ success: true, path, bytesWritten: content.length }) }],
        };
      }

      case 'pocketdev_list_directory': {
        const dirPath = arguments_.path as string;
        const includeHidden = arguments_.includeHidden as boolean || false;
        const validation = validatePath(dirPath);
        if (!validation.valid) {
          return {
            content: [{ type: 'text', text: JSON.stringify({ error: validation.error }) }],
            isError: true,
          };
        }
        const entries = await readdir(dirPath, { withFileTypes: true });
        const filtered = entries.filter(e => includeHidden || !e.name.startsWith('.'));
        const result = await Promise.all(
          filtered.map(async (entry) => ({
            name: entry.name,
            isDirectory: entry.isDirectory(),
            isFile: entry.isFile(),
            path: join(dirPath, entry.name),
          }))
        );
        return {
          content: [{ type: 'text', text: JSON.stringify({ path: dirPath, entries: result }) }],
        };
      }

      case 'pocketdev_search_files': {
        const pattern = arguments_.pattern as string;
        const root = (arguments_.root as string) || process.cwd();
        const maxResults = (arguments_.maxResults as number) || 100;
        // Simple glob matching - in production use fast-glob
        const { globSync } = await import('glob');
        const matches = globSync(pattern, { cwd: root, absolute: true, ignore: ['**/node_modules/**', '**/.git/**'] }).slice(0, maxResults);
        return {
          content: [{ type: 'text', text: JSON.stringify({ pattern, root, matches, count: matches.length }) }],
        };
      }

      case 'pocketdev_git_status': {
        const path = (arguments_.path as string) || process.cwd();
        const output = execSync('git status --porcelain', { cwd: path, encoding: 'utf-8' });
        const branch = execSync('git branch --show-current', { cwd: path, encoding: 'utf-8' }).trim();
        return {
          content: [{ type: 'text', text: JSON.stringify({ path, branch, changes: output.trim().split('\n').filter(Boolean) }) }],
        };
      }

      case 'pocketdev_git_commit': {
        const message = arguments_.message as string;
        const path = (arguments_.path as string) || process.cwd();
        const addAll = arguments_.addAll !== false;
        if (addAll) {
          execSync('git add -A', { cwd: path });
        }
        const output = execSync(`git commit -m "${message.replace(/"/g, '\\"')}"`, { cwd: path, encoding: 'utf-8' });
        return {
          content: [{ type: 'text', text: JSON.stringify({ success: true, message, output: output.trim() }) }],
        };
      }

      case 'pocketdev_git_log': {
        const path = (arguments_.path as string) || process.cwd();
        const limit = (arguments_.limit as number) || 20;
        const output = execSync(`git log --oneline -${limit}`, { cwd: path, encoding: 'utf-8' });
        return {
          content: [{ type: 'text', text: JSON.stringify({ path, commits: output.trim().split('\n') }) }],
        };
      }

      case 'pocketdev_execute_shell': {
        const command = arguments_.command as string;
        const cwd = (arguments_.cwd as string) || process.cwd();
        const timeout = ((arguments_.timeout as number) || 30) * 1000;
        const output = execSync(command, { cwd, encoding: 'utf-8', timeout });
        return {
          content: [{ type: 'text', text: JSON.stringify({ command, cwd, output: output.trim() }) }],
        };
      }

      case 'pocketdev_system_info': {
        const os = await import('os');
        const info = {
          hostname: os.hostname(),
          platform: os.platform(),
          release: os.release(),
          arch: os.arch(),
          cpus: os.cpus().length,
          totalMemory: os.totalmem(),
          freeMemory: os.freemem(),
          uptime: os.uptime(),
          nodeVersion: process.version,
        };
        return {
          content: [{ type: 'text', text: JSON.stringify(info) }],
        };
      }

      case 'pocketdev_ai_chat': {
        // Proxy to Python CLI for AI chat
        const message = arguments_.message as string;
        const model = (arguments_.model as string) || 'codellama';
        try {
          const result = execSync(
            `${POCKETDEV_CLI} -m pocketdev_cli.ai chat --model ${model} --message "${message.replace(/"/g, '\\"')}"`,
            { encoding: 'utf-8', timeout: 60000 }
          );
          return {
            content: [{ type: 'text', text: JSON.stringify({ model, response: result.trim() }) }],
          };
        } catch (error) {
          return {
            content: [{ type: 'text', text: JSON.stringify({ error: String(error) }) }],
            isError: true,
          };
        }
      }

      default:
        return {
          content: [{ type: 'text', text: `Unknown tool: ${name}` }],
          isError: true,
        };
    }
  } catch (error) {
    return {
      content: [{ type: 'text', text: JSON.stringify({ error: String(error) }) }],
      isError: true,
    };
  }
}
