import { z } from "zod";
import { env } from "../../config/env";

export const createDirectSchema = z.object({
  targetUserId: z.number().int().positive()
});

export const createGroupSchema = z.object({
  title: z.string().min(1).max(100),
  description: z.string().optional().nullable(),
  memberIds: z.array(z.number().int().positive()).min(2),
  photoMediaId: z.number().int().positive().optional().nullable()
});

export const updateGroupSchema = z.object({
  title: z.string().min(1).max(100).optional(),
  description: z.string().optional().nullable(),
  photoMediaId: z.number().int().positive().optional().nullable()
});

export const messageCreateSchema = z.object({
  messageType: z.enum(["TEXT", "IMAGE", "VIDEO", "AUDIO", "FILE", "MIXED", "SYSTEM"]),
  content: z.string().max(env.CHAT_MAX_MESSAGE_LENGTH).optional().nullable(),
  mediaIds: z.array(z.number().int().positive()).max(env.CHAT_MAX_MEDIA_PER_MESSAGE).optional(),
  replyToMessageId: z.string().uuid().optional().nullable(),
  mentionedUserIds: z.array(z.number().int().positive()).optional()
});

export const messageEditSchema = z.object({
  content: z.string().min(1).max(env.CHAT_MAX_MESSAGE_LENGTH)
});

export const muteSchema = z.object({
  mutedUntil: z.string().datetime()
});

export const roleSchema = z.object({
  role: z.enum(["OWNER", "ADMIN", "MEMBER"])
});

export const reactionSchema = z.object({
  emoji: z.string().min(1).max(10)
});

export const addMembersSchema = z.object({
  userIds: z.array(z.number().int().positive()).min(1).max(env.CHAT_MAX_GROUP_MEMBERS)
});

export const transferOwnershipSchema = z.object({
  newOwnerUserId: z.number().int().positive()
});
