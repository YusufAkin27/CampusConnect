import { env } from "../../config/env";

const userCounters = new Map<number, { lastSecond: number; count: number }>();

export function rateLimitSocket(userId: number): boolean {
  const nowSecond = Math.floor(Date.now() / 1000);
  const current = userCounters.get(userId);
  if (!current || current.lastSecond !== nowSecond) {
    userCounters.set(userId, { lastSecond: nowSecond, count: 1 });
    return true;
  }
  if (current.count >= env.CHAT_MESSAGE_RATE_LIMIT_PER_SECOND) {
    return false;
  }
  current.count += 1;
  userCounters.set(userId, current);
  return true;
}
