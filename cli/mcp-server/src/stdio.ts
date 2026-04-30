import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import {
  ListToolsRequestSchema,
  CallToolRequestSchema,
  ServerNotification,
} from '@modelcontextprotocol/sdk/types.js';
import { TOOLS, callTool } from './tools.js';

export async function createServer(): Promise<Server> {
  const server = new Server(
    {
      name: 'pocketdev',
      version: '1.0.0',
    },
    {
      capabilities: {
        tools: {},
      },
    }
  );

  server.setRequestHandler(ListToolsRequestSchema, async () => {
    return { tools: TOOLS };
  });

  server.setRequestHandler(CallToolRequestSchema, async (request) => {
    const { name, arguments: args } = request.params;
    try {
      const result = await callTool(name, args || {});
      return result;
    } catch (error) {
      return {
        content: [{ type: 'text', text: String(error) }],
        isError: true,
      };
    }
  });

  return server;
}

export async function runServer(): Promise<void> {
  const server = await createServer();
  const transport = new StdioServerTransport();
  await server.connect(transport);
}
