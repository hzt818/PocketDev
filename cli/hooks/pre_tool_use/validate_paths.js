#!/usr/bin/env node
/**
 * PreToolUse Hook: Validate file paths to prevent path traversal attacks
 *
 * This hook intercepts file operations and validates paths to ensure
 * they don't contain directory traversal sequences like ".."
 *
 * Usage (configured in hooks.json):
 *   {
 *     "matcher": "Write|Edit",
 *     "hooks": [{ "command": "node validate_paths.js" }]
 *   }
 */

const fs = require('fs');

function validatePath(inputPath) {
  if (!inputPath) {
    return { allowed: true };
  }

  // Normalize the path
  const normalized = inputPath.replace(/\\/g, '/');

  // Check for parent directory traversal
  if (normalized.includes('..')) {
    return {
      allowed: false,
      reason: 'Path traversal detected (../). This is not allowed for security reasons.'
    };
  }

  // Check for absolute paths pointing outside expected workspace
  if (inputPath.startsWith('/etc/') || inputPath.startsWith('/sys/') ||
      inputPath.startsWith('/proc/') || inputPath.startsWith('/root/')) {
    return {
      allowed: false,
      reason: 'Access to system directories is not allowed.'
    };
  }

  return { allowed: true };
}

function main() {
  // Read input from stdin
  let input;
  try {
    input = JSON.parse(fs.readFileSync(0, 'utf-8'));
  } catch (e) {
    // If no valid input, allow by default
    console.log(JSON.stringify({ action: 'allow' }));
    process.exit(0);
  }

  // Extract file paths from common tool arguments
  const args = input.arguments || {};
  let paths = [];

  // Check common file path arguments
  if (args.path) paths.push(args.path);
  if (args.paths) paths = paths.concat(args.paths);
  if (args.file) paths.push(args.file);
  if (args.file_path) paths.push(args.file_path);
  if (args.target) paths.push(args.target);
  if (args.fileName) paths.push(args.fileName);
  if (args.old_string && typeof args.old_string === 'string') {
    // For Edit operations, check if path is being manipulated
  }

  // Validate all paths
  for (const filePath of paths) {
    const validation = validatePath(filePath);
    if (!validation.allowed) {
      console.error(JSON.stringify({
        action: 'block',
        reason: validation.reason,
        tool: input.tool,
        path: filePath
      }));
      process.exit(1);
    }
  }

  // All paths valid
  console.log(JSON.stringify({ action: 'allow' }));
  process.exit(0);
}

main();
