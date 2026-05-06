import dotenv from "dotenv";
import { z } from "zod";

dotenv.config();

const envSchema = z.object({
  NODE_ENV: z.string().default("development"),
  PORT: z.coerce.number().default(8090),
  DATABASE_URL: z.string().min(1),
  REDIS_HOST: z.string().min(1),
  REDIS_PORT: z.coerce.number().default(6379),
  REDIS_PASSWORD: z.string().optional(),
  CONSUL_HOST: z.string().default("localhost"),
  CONSUL_PORT: z.coerce.number().default(8500),
  SERVICE_NAME: z.string().default("chat-service"),
  SERVICE_PORT: z.coerce.number().default(8090),
  SERVICE_ID: z.string().default("chat-service-8090"),
  AUTH_SERVICE_URL: z.string().min(1),
  USER_SERVICE_URL: z.string().min(1),
  FRIEND_SERVICE_URL: z.string().min(1),
  MEDIA_SERVICE_URL: z.string().min(1),
  LOGGING_SERVICE_URL: z.string().min(1),
  CHAT_DIRECT_ONLY_FRIENDS: z.coerce.boolean().default(false),
  CHAT_MESSAGE_RATE_LIMIT_PER_SECOND: z.coerce.number().default(5),
  CHAT_TYPING_THROTTLE_MS: z.coerce.number().default(1500),
  CHAT_MAX_GROUP_MEMBERS: z.coerce.number().default(250),
  CHAT_MAX_MEDIA_PER_MESSAGE: z.coerce.number().default(10),
  CHAT_MAX_MESSAGE_LENGTH: z.coerce.number().default(4000),
  CHAT_MESSAGE_EDIT_WINDOW_MINUTES: z.coerce.number().default(15),
  CHAT_MESSAGE_DELETE_FOR_EVERYONE_WINDOW_MINUTES: z.coerce.number().default(30),
  CHAT_MAX_PINNED_MESSAGES: z.coerce.number().default(5),
  CORS_ORIGIN: z.string().default("*")
});

const parsed = envSchema.safeParse(process.env);
if (!parsed.success) {
  throw new Error(`Invalid environment configuration: ${parsed.error.message}`);
}

export const env = parsed.data;
