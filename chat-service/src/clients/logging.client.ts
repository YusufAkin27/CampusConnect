import axios from "axios";
import { env } from "../config/env";
import { logger } from "../utils/logger";

export type LogEventRequest = {
  eventType: string;
  actorUserId?: number | null;
  referenceId?: string | null;
  message: string;
  metadata?: Record<string, unknown> | null;
  timestamp: string;
};

const client = axios.create({
  baseURL: env.LOGGING_SERVICE_URL,
  timeout: 3000
});

export const LoggingClient = {
  async logEvent(event: LogEventRequest): Promise<void> {
    try {
      await client.post("/v1/api/logs/internal/events", event);
    } catch (error) {
      logger.warn("Logging service failed", { error });
    }
  }
};
