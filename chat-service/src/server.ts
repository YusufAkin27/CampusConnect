import http from "http";
import { Server } from "socket.io";
import { createApp } from "./app";
import { env } from "./config/env";
import { initSocket } from "./config/socket";
import { logger } from "./utils/logger";
import { registerService, deregisterService } from "./config/consul";

const app = createApp();
const server = http.createServer(app);

const io = new Server(server, {
  cors: {
    origin: env.CORS_ORIGIN.split(","),
    credentials: true
  }
});

initSocket(io);

server.listen(env.PORT, async () => {
  logger.info(`Chat service running on port ${env.PORT}`);
  await registerService();
});

const shutdown = async () => {
  logger.info("Shutdown initiated");
  await deregisterService();
  server.close(() => {
    process.exit(0);
  });
};

process.on("SIGINT", shutdown);
process.on("SIGTERM", shutdown);
