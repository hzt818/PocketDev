#!/usr/bin/env node
/**
 * PreToolUse Hook: Check for dangerous shell commands
 *
 * This hook intercepts Bash tool invocations and checks
 * for potentially dangerous commands.
 */

const fs = require('fs');

// Dangerous patterns that should require extra scrutiny
const DANGEROUS_PATTERNS = [
  { pattern: /rm\s+(-rf\s+\/|-\s*rf)/i, reason: 'Recursive force delete' },
  { pattern: /dd\s+if=/i, reason: 'Direct disk write' },
  { pattern: /mkfs/i, reason: 'Filesystem format' },
  { pattern: /fdisk/i, reason: 'Disk partitioning' },
  { pattern: /curl\s+.*\|\s*sh/i, reason: 'Pipe to shell (curl sh)' },
  { pattern: /wget\s+.*\|\s*sh/i, reason: 'Pipe to shell (wget sh)' },
  { pattern: /:()\s*\{/i, reason: 'Fork bomb pattern' },
  { pattern: />\s*\/dev\/sda/i, reason: 'Direct write to block device' },
  { pattern: /chmod\s+777/i, reason: 'World-writable permissions' },
  { pattern: /chmod\s+4755/i, reason: 'SetUID permission' },
  { pattern: /ssh\s+.*@.*\s+('|")/i, reason: 'Inline SSH with password' },
  { pattern: /git\s+push\s+.*--force/i, reason: 'Force push' },
  { pattern: /docker\s+run\s+.*--privileged/i, reason: 'Privileged container' },
  { pattern: /:\(\)\s*\{/i, reason: 'Fork bomb syntax' },
];

// Commands that are always blocked
const BLOCKED_COMMANDS = [
  'shutdown',
  'reboot',
  'halt',
  'poweroff',
  'init 0',
  'init 6',
];

function checkDangerous(command) {
  // Check blocked commands
  for (const blocked of BLOCKED_COMMANDS) {
    if (command.includes(blocked)) {
      return { allowed: false, reason: `Blocked command: ${blocked}` };
    }
  }

  // Check dangerous patterns
  for (const { pattern, reason } of DANGEROUS_PATTERNS) {
    if (pattern.test(command)) {
      return {
        allowed: false,
        reason: `Dangerous pattern detected: ${reason}`
      };
    }
  }

  return { allowed: true };
}

function main() {
  let input;
  try {
    input = JSON.parse(fs.readFileSync(0, 'utf-8'));
  } catch (e) {
    console.log(JSON.stringify({ action: 'allow' }));
    process.exit(0);
  }

  // Only check Bash tool
  if (input.tool !== 'Bash') {
    console.log(JSON.stringify({ action: 'allow' }));
    process.exit(0);
  }

  const command = input.arguments?.command || input.arguments?.cmd || '';

  if (!command) {
    console.log(JSON.stringify({ action: 'allow' }));
    process.exit(0);
  }

  const check = checkDangerous(command);
  if (!check.allowed) {
    console.error(JSON.stringify({
      action: 'block',
      reason: check.reason,
      tool: 'Bash',
      command: command.substring(0, 100) + (command.length > 100 ? '...' : '')
    }));
    process.exit(1);
  }

  console.log(JSON.stringify({ action: 'allow' }));
  process.exit(0);
}

main();
