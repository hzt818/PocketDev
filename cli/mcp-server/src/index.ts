#!/usr/bin/env node

/**
 * PocketDev MCP Server
 *
 * MCP (Model Context Protocol) server for Claude Code integration.
 * Provides tools for file operations, git workflows, and AI chat.
 *
 * Usage:
 *   node dist/index.js
 *
 * Environment Variables:
 *   POCKETDEV_CLI - Path to Python CLI (default: python)
 *   POCKETDEV_HOST - Host for AI proxy (default: localhost)
 *   POCKETDEV_PORT - Port for AI proxy (default: 8080)
 *   POCKETDEV_API_KEY - API key for authentication
 */

import { runServer } from './stdio.js';

async function main() {
  try {
    console.error('[pocketdev-mcp] Starting MCP server...');
    await runServer();
  } catch (error) {
    console.error('[pocketdev-mcp] Fatal error:', error);
    process.exit(1);
  }
}

main();
