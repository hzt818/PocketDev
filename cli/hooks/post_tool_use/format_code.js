#!/usr/bin/env node
/**
 * PostToolUse Hook: Auto-format code after edits
 *
 * This hook runs after Edit or Write operations to automatically
 * format the modified code files.
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

// Map of file extensions to formatters
const FORMATTERS = {
  '.py': 'black --quiet',
  '.kt': 'ktlint --quiet',
  '.kts': 'ktlint --quiet',
  '.js': 'prettier --write --quiet',
  '.ts': 'prettier --write --quiet',
  '.tsx': 'prettier --write --quiet',
  '.jsx': 'prettier --write --quiet',
  '.json': 'prettier --write --quiet',
  '.css': 'prettier --write --quiet',
  '.scss': 'prettier --write --quiet',
  '.html': 'prettier --write --quiet',
  '.md': 'prettier --write --quiet',
  '.yaml': 'prettier --write --quiet',
  '.yml': 'prettier --write --quiet',
  '.xml': 'prettier --write --quiet',
};

// Directories to ignore
const IGNORED_DIRS = ['node_modules', '.git', 'build', 'dist', '.gradle', '__pycache__'];

function shouldFormat(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  const dirParts = filePath.split(path.sep);

  // Check if any part of the path is in ignored directories
  for (const part of dirParts) {
    if (IGNORED_DIRS.includes(part)) {
      return false;
    }
  }

  return ext in FORMATTERS;
}

function getFormatter(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  return FORMATTERS[ext];
}

function formatFile(filePath) {
  if (!shouldFormat(filePath)) {
    return { skipped: true };
  }

  const formatter = getFormatter(filePath);
  try {
    execSync(`${formatter} "${filePath}"`, { stdio: 'pipe' });
    return { success: true, formatter };
  } catch (e) {
    // Non-blocking - formatter might not be installed
    return { success: false, error: e.message };
  }
}

function main() {
  let input;
  try {
    input = JSON.parse(fs.readFileSync(0, 'utf-8'));
  } catch (e) {
    process.exit(0);
  }

  // Only run on Edit or Write tools
  if (input.tool !== 'Edit' && input.tool !== 'Write') {
    process.exit(0);
  }

  // Extract file path from result
  let filePath = null;

  // Check result for file path
  if (input.result) {
    if (typeof input.result === 'string') {
      // Try to extract path from result
      const match = input.result.match(/[A-Za-z]:[\\\/]|[\\\/][^\\\/]+)+/);
      if (match) {
        filePath = match[0];
      }
    } else if (input.result.filePath) {
      filePath = input.result.filePath;
    } else if (input.result.path) {
      filePath = input.result.path;
    }
  }

  // Also check arguments
  if (!filePath && input.arguments) {
    filePath = input.arguments.file_path || input.arguments.path || input.arguments.target;
  }

  if (!filePath) {
    process.exit(0);
  }

  // Normalize path
  filePath = path.normalize(filePath);

  const result = formatFile(filePath);

  if (result.skipped) {
    console.log(JSON.stringify({ action: 'skip', reason: 'Not a formattable file' }));
  } else if (result.success) {
    console.log(JSON.stringify({ action: 'format', formatter: result.formatter, file: filePath }));
  } else {
    console.log(JSON.stringify({ action: 'warn', reason: result.error, file: filePath }));
  }

  process.exit(0);
}

main();
