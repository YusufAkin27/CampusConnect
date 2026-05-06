import { Prisma, PrismaClient } from "@prisma/client";
import { prisma } from "../../config/prisma";

export class ChatRepository {
  private db: PrismaClient;

  constructor(client: PrismaClient = prisma) {
    this.db = client;
  }

  findDirectConversation(userId: number, targetUserId: number) {
    return this.db.conversation.findFirst({
      where: {
        type: "DIRECT",
        participants: {
          some: { userId }
        },
        AND: {
          participants: { some: { userId: targetUserId } }
        }
      },
      include: { participants: true }
    });
  }

  createConversation(data: Prisma.ConversationCreateInput) {
    return this.db.conversation.create({ data });
  }

  updateConversation(conversationId: string, data: Prisma.ConversationUpdateInput) {
    return this.db.conversation.update({ where: { id: conversationId }, data });
  }

  getConversation(conversationId: string) {
    return this.db.conversation.findUnique({ where: { id: conversationId } });
  }

  listUserParticipants(userId: number, params: { skip: number; take: number }) {
    return this.db.conversationParticipant.findMany({
      where: { userId },
      include: { conversation: true },
      skip: params.skip,
      take: params.take,
      orderBy: { conversation: { updatedAt: "desc" } }
    });
  }

  countUserParticipants(userId: number) {
    return this.db.conversationParticipant.count({ where: { userId } });
  }

  findParticipants(conversationId: string) {
    return this.db.conversationParticipant.findMany({ where: { conversationId } });
  }

  findParticipant(conversationId: string, userId: number) {
    return this.db.conversationParticipant.findUnique({
      where: { conversationId_userId: { conversationId, userId } }
    });
  }

  upsertParticipant(conversationId: string, userId: number, data: Prisma.ConversationParticipantUpdateInput,
                   create: Prisma.ConversationParticipantCreateInput) {
    return this.db.conversationParticipant.upsert({
      where: { conversationId_userId: { conversationId, userId } },
      update: data,
      create
    });
  }

  updateParticipant(conversationId: string, userId: number, data: Prisma.ConversationParticipantUpdateInput) {
    return this.db.conversationParticipant.update({
      where: { conversationId_userId: { conversationId, userId } },
      data
    });
  }

  createMessage(data: Prisma.MessageCreateInput) {
    return this.db.message.create({ data });
  }

  updateMessage(messageId: string, data: Prisma.MessageUpdateInput) {
    return this.db.message.update({ where: { id: messageId }, data });
  }

  findMessage(messageId: string) {
    return this.db.message.findUnique({ where: { id: messageId } });
  }

  listMessages(conversationId: string, skip: number, take: number, userId?: number) {
    return this.db.message.findMany({
      where: {
        conversationId,
        deleteStates: userId ? { none: { userId } } : undefined
      },
      orderBy: { createdAt: "desc" },
      skip,
      take
    });
  }

  countMessages(conversationId: string, userId?: number) {
    return this.db.message.count({
      where: {
        conversationId,
        deleteStates: userId ? { none: { userId } } : undefined
      }
    });
  }

  createMessageMedia(data: Prisma.MessageMediaCreateManyInput[]) {
    return this.db.messageMedia.createMany({ data });
  }

  listMessageMedia(messageId: string) {
    return this.db.messageMedia.findMany({ where: { messageId }, orderBy: { orderIndex: "asc" } });
  }

  listMessageReactions(messageId: string) {
    return this.db.messageReaction.findMany({ where: { messageId } });
  }

  createReaction(data: Prisma.MessageReactionCreateInput) {
    return this.db.messageReaction.create({ data });
  }

  deleteReaction(messageId: string, userId: number, emoji: string) {
    return this.db.messageReaction.delete({
      where: { messageId_userId_emoji: { messageId, userId, emoji } }
    });
  }

  createReadReceipt(data: Prisma.MessageReadReceiptCreateInput) {
    return this.db.messageReadReceipt.create({ data });
  }

  createDeliveryReceipt(data: Prisma.MessageDeliveryReceiptCreateInput) {
    return this.db.messageDeliveryReceipt.create({ data });
  }

  upsertDeleteState(messageId: string, userId: number) {
    return this.db.messageDeleteState.upsert({
      where: { messageId_userId: { messageId, userId } },
      update: { deletedAt: new Date() },
      create: { messageId, userId }
    });
  }

  countPinnedMessages(conversationId: string) {
    return this.db.pinnedMessage.count({ where: { conversationId } });
  }

  createPinnedMessage(data: Prisma.PinnedMessageCreateInput) {
    return this.db.pinnedMessage.create({ data });
  }

  deletePinnedMessage(conversationId: string, messageId: string) {
    return this.db.pinnedMessage.deleteMany({ where: { conversationId, messageId } });
  }

  listPinnedMessages(conversationId: string) {
    return this.db.pinnedMessage.findMany({ where: { conversationId } });
  }

  createAudit(data: Prisma.ChatAuditCreateInput) {
    return this.db.chatAudit.create({ data });
  }

  createMentions(data: Prisma.MessageMentionCreateManyInput[]) {
    return this.db.messageMention.createMany({ data });
  }

  listDeleteStates(messageId: string, userId: number) {
    return this.db.messageDeleteState.findUnique({ where: { messageId_userId: { messageId, userId } } });
  }
}
