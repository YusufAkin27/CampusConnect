import { logger } from "../utils/logger";

export type NotificationPayload = {
  type: string;
  userId: number;
  data: Record<string, unknown>;
};

export const NotificationPublisher = {
  async publishNewMessageNotification(payload: NotificationPayload): Promise<void> {
    logger.info("Notification publish", payload);
  },

  async publishGroupInviteNotification(payload: NotificationPayload): Promise<void> {
    logger.info("Notification publish", payload);
  },

  async publishMentionNotification(payload: NotificationPayload): Promise<void> {
    logger.info("Notification publish", payload);
  },

  async publishMessageReactionNotification(payload: NotificationPayload): Promise<void> {
    logger.info("Notification publish", payload);
  }
};
