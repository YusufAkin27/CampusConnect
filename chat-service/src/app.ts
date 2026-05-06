import express from "express";
import cors from "cors";
import helmet from "helmet";
import { env } from "./config/env";
import { requestLogger } from "./middlewares/request-logger.middleware";
import { errorMiddleware } from "./middlewares/error.middleware";
import { healthRoutes } from "./health/health.routes";
import { chatRoutes } from "./modules/chat/chat.routes";

export function createApp() {
  const app = express();

  app.use(helmet());
  app.use(cors({
    origin: env.CORS_ORIGIN.split(","),
    credentials: true
  }));
  app.use(express.json({ limit: "2mb" }));
  app.use(requestLogger);

  app.use(healthRoutes);
  app.use("/v1/api/chats", chatRoutes);

  app.use(errorMiddleware);

  return app;
}
