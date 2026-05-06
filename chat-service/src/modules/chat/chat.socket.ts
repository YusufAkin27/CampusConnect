import { Namespace, Socket } from "socket.io";
import { MessageType } from "@prisma/client";
import { ChatService } from "./chat.service";
import { rateLimitSocket } from "../chat/chat.socket.utils";
import { redis } from "../../config/redis";

export class ChatSocketHandler {
  private static namespace: Namespace | null = null;
  private static service = new ChatService();

  static register(socket: Socket, nsp: Namespace): void {
    ChatSocketHandler.namespace = nsp;
    const user = socket.data.user;

    socket.join(`user:${user.userId}`);

    redis.set(`presence:user:${user.userId}`, "online");
    redis.sadd(`sockets:user:${user.userId}`, socket.id);
    nsp.emit("presence:online", { userId: user.userId });

    socket.on("conversation:join", async (payload) => {
      socket.join(`conversation:${payload.conversationId}`);
    });

    socket.on("conversation:leave", async (payload) => {
      socket.leave(`conversation:${payload.conversationId}`);
    });

    socket.on("typing:start", async (payload) => {
      const key = `typing:conversation:${payload.conversationId}:${user.userId}`;
      await redis.set(key, "true", "PX", 5000);
      nsp.to(`conversation:${payload.conversationId}`).emit("typing:start", {
        conversationId: payload.conversationId,
        userId: user.userId,
        username: user.username
      });
    });

    socket.on("typing:stop", async (payload) => {
      const key = `typing:conversation:${payload.conversationId}:${user.userId}`;
      await redis.del(key);
      nsp.to(`conversation:${payload.conversationId}`).emit("typing:stop", {
        conversationId: payload.conversationId,
        userId: user.userId
      });
    });

    socket.on("message:send", async (payload) => {
      if (!rateLimitSocket(user.userId)) {
        socket.emit("error", { code: "CHAT_ERROR", message: "Rate limit exceeded" });
        return;
      }
      try {
        const message = await ChatSocketHandler.service.sendMessage(user, payload.conversationId, {
          messageType: payload.messageType as MessageType,
          content: payload.content,
          mediaIds: payload.mediaIds,
          replyToMessageId: payload.replyToMessageId,
          mentionedUserIds: payload.mentionedUserIds
        });
        socket.emit("message:sent", { clientMessageId: payload.clientMessageId, message });
      } catch (error: any) {
        socket.emit("error", { code: "CHAT_ERROR", message: error.message || "Send failed" });
      }
    });

    socket.on("message:read", async (payload) => {
      try {
        await ChatSocketHandler.service.markRead(user, payload.conversationId, payload.messageId);
      } catch (error: any) {
        socket.emit("error", { code: "CHAT_ERROR", message: error.message || "Read failed" });
      }
    });

    socket.on("presence:get", async (payload) => {
      const presence = await Promise.all(
        payload.userIds.map(async (id: number) => ({
          userId: id,
          online: Boolean(await redis.get(`presence:user:${id}`))
        }))
      );
      socket.emit("presence:online", presence);
    });

    socket.on("disconnect", async () => {
      await redis.srem(`sockets:user:${user.userId}`, socket.id);
      const remaining = await redis.scard(`sockets:user:${user.userId}`);
      if (remaining === 0) {
        await redis.del(`presence:user:${user.userId}`);
        nsp.emit("presence:offline", { userId: user.userId });
      }
    });
  }

  static emitNewMessage(conversationId: string, message: any): void {
    if (!ChatSocketHandler.namespace) {
      return;
    }
    ChatSocketHandler.namespace.to(`conversation:${conversationId}`).emit("message:new", message);
  }

  static emitMessageEdited(conversationId: string, message: any): void {
    if (!ChatSocketHandler.namespace) {
      return;
    }
    ChatSocketHandler.namespace.to(`conversation:${conversationId}`).emit("message:edited", message);
  }

  static emitMessageDeleted(conversationId: string, messageId: string, deletedForEveryone: boolean): void {
    if (!ChatSocketHandler.namespace) {
      return;
    }
    ChatSocketHandler.namespace.to(`conversation:${conversationId}`).emit("message:deleted", {
      conversationId,
      messageId,
      deletedForEveryone
    });
  }

  static emitMessageRead(conversationId: string, messageId: string, userId: number): void {
    if (!ChatSocketHandler.namespace) {
      return;
    }
    ChatSocketHandler.namespace.to(`conversation:${conversationId}`).emit("message:read", {
      conversationId,
      messageId,
      userId,
      readAt: new Date().toISOString()
    });
  }

  static emitReactionAdded(conversationId: string, reaction: any): void {
    if (!ChatSocketHandler.namespace) {
      return;
    }
    ChatSocketHandler.namespace.to(`conversation:${conversationId}`).emit("reaction:added", reaction);
  }

  static emitReactionRemoved(conversationId: string, messageId: string, userId: number, emoji: string): void {
    if (!ChatSocketHandler.namespace) {
      return;
    }
    ChatSocketHandler.namespace.to(`conversation:${conversationId}`).emit("reaction:removed", {
      messageId,
      userId,
      emoji
    });
  }

  static emitConversationUpdated(conversationId: string, payload: any): void {
    if (!ChatSocketHandler.namespace) {
      return;
    }
    ChatSocketHandler.namespace.to(`conversation:${conversationId}`).emit("conversation:updated", payload);
  }
}
