import { NextFunction, Request, Response } from "express";
import { env } from "../config/env";
import { redis } from "../config/redis";
import { RateLimitExceededError } from "../errors/chat.errors";

export async function rateLimitMiddleware(req: Request, res: Response, next: NextFunction): Promise<void> {
  const userId = (req as any).user?.userId || req.ip;
  const key = `chat:rl:${userId}:${Math.floor(Date.now() / 1000)}`;
  const count = await redis.incr(key);
  if (count === 1) {
    await redis.expire(key, 1);
  }
  if (count > env.CHAT_MESSAGE_RATE_LIMIT_PER_SECOND) {
    next(new RateLimitExceededError());
    return;
  }
  next();
}
