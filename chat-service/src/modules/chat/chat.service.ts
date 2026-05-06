import { MessageType, ParticipantRole, ParticipantStatus } from "@prisma/client";
import { env } from "../../config/env";
import { redis } from "../../config/redis";
import { FriendClient } from "../../clients/friend.client";
import { LoggingClient } from "../../clients/logging.client";
import { MediaClient } from "../../clients/media.client";
import { UserClient } from "../../clients/user.client";
import {
  ConversationAccessDeniedError,
  ConversationNotFoundError,
  DirectConversationAlreadyExistsError,
  ExternalServiceUnavailableError,
  GroupAdminRequiredError,
  GroupOwnerRequiredError,
  MediaValidationFailedError,
  MessageAlreadyDeletedError,
  MessageDeleteWindowExpiredError,
  MessageEditWindowExpiredError,
  MessageNotFoundError,
  ParticipantAlreadyExistsError,
  ParticipantNotFoundError,
  ParticipantPermissionDeniedError,
  UserBlockedError,
  UserNotFoundError
} from "../../errors/chat.errors";
import { NotificationPublisher } from "../../publishers/notification.publisher";
import { ChatMapper } from "./chat.mapper";
import { ChatRepository } from "./chat.repository";
import {
  ConversationDetailResponse,
  ConversationListItemResponse,
  MessageReactionResponse,
  MessageResponse,
  UserContext
} from "./chat.types";
import { ChatSocketHandler } from "./chat.socket";

export class ChatService {
  private repository: ChatRepository;

  constructor(repo: ChatRepository = new ChatRepository()) {
    this.repository = repo;
  }

  async createDirectConversation(user: UserContext, targetUserId: number): Promise<ConversationDetailResponse> {
    if (user.userId === targetUserId) {
      throw new ParticipantPermissionDeniedError("Cannot create direct chat with yourself");
    }

    try {
      const exists = await UserClient.existsUser(targetUserId);
      if (!exists) {
        throw new UserNotFoundError();
      }
    } catch (error) {
      if (error instanceof UserNotFoundError) {
        throw error;
      }
      throw new ExternalServiceUnavailableError("User service unavailable");
    }

    try {
      if (await FriendClient.isBlockedByEitherSide(user.userId, targetUserId)) {
        throw new UserBlockedError();
      }

      if (env.CHAT_DIRECT_ONLY_FRIENDS) {
        const allowed = await FriendClient.canSendDirectMessage(user.userId, targetUserId);
        if (!allowed) {
          throw new ParticipantPermissionDeniedError("Direct messaging not allowed");
        }
      }
    } catch (error) {
      if (error instanceof UserBlockedError || error instanceof ParticipantPermissionDeniedError) {
        throw error;
      }
      throw new ExternalServiceUnavailableError("Friend service unavailable");
    }

    const existing = await this.repository.findDirectConversation(user.userId, targetUserId);
    if (existing) {
      const participants = await this.repository.findParticipants(existing.id);
      return ChatMapper.toConversationDetail(existing, participants);
    }

    const conversation = await this.repository.createConversation({
      type: "DIRECT",
      createdByUserId: user.userId,
      participants: {
        create: [
          {
            userId: user.userId,
            role: ParticipantRole.MEMBER,
            status: ParticipantStatus.ACTIVE,
            usernameSnapshot: user.username,
            fullNameSnapshot: user.fullName
          },
          {
            userId: targetUserId,
            role: ParticipantRole.MEMBER,
            status: ParticipantStatus.ACTIVE
          }
        ]
      }
    });

    await this.repository.createAudit({
      conversationId: conversation.id,
      actorUserId: user.userId,
      action: "CONVERSATION_CREATED",
      description: "Direct conversation created"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_CONVERSATION_CREATED",
      actorUserId: user.userId,
      referenceId: conversation.id,
      message: "Direct conversation created",
      timestamp: new Date().toISOString()
    });

    const participants = await this.repository.findParticipants(conversation.id);
    return ChatMapper.toConversationDetail(conversation, participants);
  }

  async createGroupConversation(user: UserContext, input: { title: string; description?: string | null; memberIds: number[]; photoMediaId?: number | null; }): Promise<ConversationDetailResponse> {
    const uniqueMembers = Array.from(new Set(input.memberIds)).filter((id) => id !== user.userId);
    if (uniqueMembers.length < 2) {
      throw new ParticipantPermissionDeniedError("Group must have at least 2 other members");
    }

    let memberSnapshots: { id: number; username: string; fullName: string; profilePhotoUrl?: string | null }[] = [];
    try {
      memberSnapshots = await UserClient.getUsersByIds(uniqueMembers);
    } catch (error) {
      throw new ExternalServiceUnavailableError("User service unavailable");
    }

    if (memberSnapshots.length !== uniqueMembers.length) {
      throw new UserNotFoundError("One or more users not found");
    }

    const mediaValid = input.photoMediaId ? await MediaClient.validateMediaIds([input.photoMediaId]) : true;
    if (!mediaValid) {
      throw new MediaValidationFailedError();
    }

    const conversation = await this.repository.createConversation({
      type: "GROUP",
      title: input.title,
      description: input.description ?? null,
      photoMediaId: input.photoMediaId ?? null,
      createdByUserId: user.userId,
      participants: {
        create: [
          {
            userId: user.userId,
            role: ParticipantRole.OWNER,
            status: ParticipantStatus.ACTIVE,
            usernameSnapshot: user.username,
            fullNameSnapshot: user.fullName
          },
          ...memberSnapshots.map((member) => ({
            userId: member.id,
            role: ParticipantRole.MEMBER,
            status: ParticipantStatus.ACTIVE,
            usernameSnapshot: member.username,
            fullNameSnapshot: member.fullName,
            profilePhotoSnapshot: member.profilePhotoUrl ?? null
          }))
        ]
      }
    });

    await this.repository.createAudit({
      conversationId: conversation.id,
      actorUserId: user.userId,
      action: "GROUP_CREATED",
      description: "Group conversation created"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_GROUP_CREATED",
      actorUserId: user.userId,
      referenceId: conversation.id,
      message: "Group conversation created",
      timestamp: new Date().toISOString()
    });

    const participants = await this.repository.findParticipants(conversation.id);
    return ChatMapper.toConversationDetail(conversation, participants);
  }

  async listConversations(user: UserContext, params: { page: number; size: number; type?: string; archived?: boolean; pinned?: boolean; }): Promise<{ data: ConversationListItemResponse[]; total: number; }> {
    const skip = params.page * params.size;
    const participants = await this.repository.listUserParticipants(user.userId, { skip, take: params.size });

    const filtered = participants.filter((p) => {
      if (params.type && p.conversation.type !== params.type) {
        return false;
      }
      if (params.archived !== undefined && p.archived !== params.archived) {
        return false;
      }
      if (params.pinned !== undefined && p.pinned !== params.pinned) {
        return false;
      }
      return true;
    });

    const total = await this.repository.countUserParticipants(user.userId);
    return {
      data: filtered.map((p) => ChatMapper.toConversationListItem(p, p.conversation)),
      total
    };
  }

  async getConversationDetail(user: UserContext, conversationId: string): Promise<ConversationDetailResponse> {
    const conversation = await this.repository.getConversation(conversationId);
    if (!conversation) {
      throw new ConversationNotFoundError();
    }
    const participant = await this.repository.findParticipant(conversationId, user.userId);
    if (!participant || participant.status !== ParticipantStatus.ACTIVE) {
      throw new ConversationAccessDeniedError();
    }
    const participants = await this.repository.findParticipants(conversationId);
    return ChatMapper.toConversationDetail(conversation, participants);
  }

  async updateGroup(user: UserContext, conversationId: string, input: { title?: string; description?: string | null; photoMediaId?: number | null; }): Promise<ConversationDetailResponse> {
    const conversation = await this.getConversationOrThrow(conversationId);
    if (conversation.type !== "GROUP") {
      throw new ConversationAccessDeniedError("Only group conversations can be updated");
    }
    await this.requireAdmin(user.userId, conversationId);

    if (input.photoMediaId) {
      const valid = await MediaClient.validateMediaIds([input.photoMediaId]);
      if (!valid) {
        throw new MediaValidationFailedError();
      }
    }

    const updated = await this.repository.updateConversation(conversationId, {
      title: input.title ?? undefined,
      description: input.description ?? undefined,
      photoMediaId: input.photoMediaId ?? undefined
    });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "GROUP_UPDATED",
      description: "Group updated"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_GROUP_UPDATED",
      actorUserId: user.userId,
      referenceId: conversationId,
      message: "Group updated",
      timestamp: new Date().toISOString()
    });

    const participants = await this.repository.findParticipants(conversationId);
    ChatSocketHandler.emitConversationUpdated(conversationId, ChatMapper.toConversationDetail(updated, participants));
    return ChatMapper.toConversationDetail(updated, participants);
  }

  async leaveConversation(user: UserContext, conversationId: string): Promise<void> {
    const participant = await this.repository.findParticipant(conversationId, user.userId);
    if (!participant) {
      throw new ParticipantNotFoundError();
    }

    if (participant.role === ParticipantRole.OWNER) {
      await this.transferOwnershipOnLeave(conversationId, user.userId);
    }

    await this.repository.updateParticipant(conversationId, user.userId, {
      status: ParticipantStatus.LEFT,
      leftAt: new Date()
    });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "USER_LEFT",
      description: "User left"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_USER_LEFT",
      actorUserId: user.userId,
      referenceId: conversationId,
      message: "User left conversation",
      timestamp: new Date().toISOString()
    });
  }

  async archiveConversation(user: UserContext, conversationId: string, archived: boolean): Promise<void> {
    await this.repository.updateParticipant(conversationId, user.userId, { archived });
  }

  async pinConversation(user: UserContext, conversationId: string, pinned: boolean): Promise<void> {
    await this.repository.updateParticipant(conversationId, user.userId, { pinned });
  }

  async muteConversation(user: UserContext, conversationId: string, mutedUntil: string | null): Promise<void> {
    await this.repository.updateParticipant(conversationId, user.userId, {
      mutedUntil: mutedUntil ? new Date(mutedUntil) : null
    });
  }

  async listMembers(user: UserContext, conversationId: string) {
    await this.ensureActiveParticipant(conversationId, user.userId);
    return this.repository.findParticipants(conversationId);
  }

  async addMembers(user: UserContext, conversationId: string, userIds: number[]): Promise<void> {
    await this.requireAdmin(user.userId, conversationId);
    const uniqueIds = Array.from(new Set(userIds)).filter((id) => id !== user.userId);

    const participants = await this.repository.findParticipants(conversationId);
    const existingIds = new Set(participants.map((p) => p.userId));
    const newIds = uniqueIds.filter((id) => !existingIds.has(id));

    let snapshots: { id: number; username: string; fullName: string; profilePhotoUrl?: string | null }[] = [];
    try {
      snapshots = await UserClient.getUsersByIds(newIds);
    } catch (error) {
      throw new ExternalServiceUnavailableError("User service unavailable");
    }

    if (snapshots.length !== newIds.length) {
      throw new UserNotFoundError("One or more users not found");
    }

    for (const snapshot of snapshots) {
      await this.repository.upsertParticipant(
        conversationId,
        snapshot.id,
        { status: ParticipantStatus.ACTIVE, leftAt: null, removedAt: null },
        {
          conversation: { connect: { id: conversationId } },
          userId: snapshot.id,
          role: ParticipantRole.MEMBER,
          status: ParticipantStatus.ACTIVE,
          usernameSnapshot: snapshot.username,
          fullNameSnapshot: snapshot.fullName,
          profilePhotoSnapshot: snapshot.profilePhotoUrl ?? null
        }
      );
    }

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "USER_ADDED",
      description: "Users added"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_USER_ADDED",
      actorUserId: user.userId,
      referenceId: conversationId,
      message: "Users added",
      timestamp: new Date().toISOString()
    });
  }

  async removeMember(user: UserContext, conversationId: string, targetUserId: number): Promise<void> {
    const requester = await this.repository.findParticipant(conversationId, user.userId);
    if (!requester) {
      throw new ParticipantNotFoundError();
    }
    if (requester.role === ParticipantRole.MEMBER) {
      throw new GroupAdminRequiredError();
    }

    const target = await this.repository.findParticipant(conversationId, targetUserId);
    if (!target) {
      throw new ParticipantNotFoundError();
    }
    if (requester.role === ParticipantRole.ADMIN && target.role === ParticipantRole.OWNER) {
      throw new ParticipantPermissionDeniedError("Admin cannot remove owner");
    }

    await this.repository.updateParticipant(conversationId, targetUserId, {
      status: ParticipantStatus.REMOVED,
      removedAt: new Date(),
      removedByUserId: user.userId
    });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      targetUserId,
      action: "USER_REMOVED",
      description: "User removed"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_USER_REMOVED",
      actorUserId: user.userId,
      referenceId: conversationId,
      message: "User removed",
      timestamp: new Date().toISOString()
    });
  }

  async changeRole(user: UserContext, conversationId: string, targetUserId: number, role: ParticipantRole): Promise<void> {
    await this.requireOwner(user.userId, conversationId);
    await this.repository.updateParticipant(conversationId, targetUserId, { role });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      targetUserId,
      action: "ROLE_CHANGED",
      description: "Role changed"
    });
  }

  async transferOwnership(user: UserContext, conversationId: string, newOwnerUserId: number): Promise<void> {
    await this.requireOwner(user.userId, conversationId);
    await this.repository.updateParticipant(conversationId, user.userId, { role: ParticipantRole.ADMIN });
    await this.repository.updateParticipant(conversationId, newOwnerUserId, { role: ParticipantRole.OWNER });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      targetUserId: newOwnerUserId,
      action: "ROLE_CHANGED",
      description: "Ownership transferred"
    });
  }

  async sendMessage(user: UserContext, conversationId: string, input: { messageType: MessageType; content?: string | null; mediaIds?: number[]; replyToMessageId?: string | null; mentionedUserIds?: number[]; }): Promise<MessageResponse> {
    const participant = await this.ensureActiveParticipant(conversationId, user.userId);

    if (participant.status !== ParticipantStatus.ACTIVE) {
      throw new ParticipantPermissionDeniedError("Participant not active");
    }

    const conversation = await this.getConversationOrThrow(conversationId);
    if (conversation.type === "DIRECT") {
      const otherUserId = await this.getDirectTargetUserId(conversationId, user.userId);
      if (await FriendClient.isBlockedByEitherSide(user.userId, otherUserId)) {
        throw new UserBlockedError();
      }
      if (env.CHAT_DIRECT_ONLY_FRIENDS) {
        const allowed = await FriendClient.canSendDirectMessage(user.userId, otherUserId);
        if (!allowed) {
          throw new ParticipantPermissionDeniedError("Direct messaging not allowed");
        }
      }
    }

    this.validateMessageInput(input);

    if (input.replyToMessageId) {
      const replyMessage = await this.repository.findMessage(input.replyToMessageId);
      if (!replyMessage || replyMessage.conversationId !== conversationId) {
        throw new MessageNotFoundError("Reply message not found");
      }
    }

    const mediaIds = input.mediaIds ?? [];
    let mediaSummaries = [] as { mediaId: number; url: string; mediaType: string; fileName?: string | null; size?: number | null; thumbnailUrl?: string | null; }[];

    if (mediaIds.length > 0) {
      const valid = await MediaClient.validateMediaIds(mediaIds);
      if (!valid) {
        throw new MediaValidationFailedError();
      }
      mediaSummaries = await MediaClient.getMediaSummaries(mediaIds);
    }

    const message = await this.repository.createMessage({
      conversation: { connect: { id: conversationId } },
      senderId: user.userId,
      messageType: input.messageType,
      content: input.content ?? null,
      replyToMessageId: input.replyToMessageId ?? null
    });

    if (mediaSummaries.length > 0) {
      await this.repository.createMessageMedia(
        mediaSummaries.map((media, index) => ({
          messageId: message.id,
          mediaId: media.mediaId,
          mediaUrlSnapshot: media.url,
          mediaType: media.mediaType,
          fileName: media.fileName ?? null,
          size: media.size ?? null,
          thumbnailUrl: media.thumbnailUrl ?? null,
          orderIndex: index,
          createdAt: new Date()
        }))
      );

      MediaClient.registerMediaUsage(mediaIds, "CHAT_MESSAGE", message.id).catch(() => undefined);
    }

    if (input.mentionedUserIds && input.mentionedUserIds.length > 0) {
      const mentioned = await this.filterValidMentionedUsers(conversationId, input.mentionedUserIds);
      if (mentioned.length > 0) {
        await this.repository.createMentions(
          mentioned.map((userId) => ({ messageId: message.id, mentionedUserId: userId, createdAt: new Date() }))
        );
      }
    }

    await this.repository.updateConversation(conversationId, {
      lastMessageId: message.id,
      lastMessageTextSnapshot: message.content,
      lastMessageAt: message.createdAt
    });

    await this.incrementUnreadCounts(conversationId, user.userId);

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_SENT",
      messageId: message.id,
      description: "Message sent"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_MESSAGE_SENT",
      actorUserId: user.userId,
      referenceId: message.id,
      message: "Message sent",
      timestamp: new Date().toISOString()
    });

    const media = await this.repository.listMessageMedia(message.id);
    const reactions = await this.repository.listMessageReactions(message.id);
    const response = ChatMapper.toMessageResponse(message, media, reactions);

    ChatSocketHandler.emitNewMessage(conversationId, response);

    await this.publishNotifications(conversationId, user.userId, response, input.mentionedUserIds || []);

    return response;
  }

  async listMessages(user: UserContext, conversationId: string, page: number, size: number) {
    await this.ensureActiveParticipant(conversationId, user.userId);
    const messages = await this.repository.listMessages(conversationId, page * size, size, user.userId);
    const total = await this.repository.countMessages(conversationId, user.userId);
    const results: MessageResponse[] = [];
    for (const message of messages) {
      const media = await this.repository.listMessageMedia(message.id);
      const reactions = await this.repository.listMessageReactions(message.id);
      results.push(ChatMapper.toMessageResponse(message, media, reactions));
    }
    return { data: results, total };
  }

  async editMessage(user: UserContext, conversationId: string, messageId: string, content: string): Promise<MessageResponse> {
    const message = await this.requireMessageOwner(conversationId, messageId, user.userId);
    this.ensureEditable(message);

    const updated = await this.repository.updateMessage(messageId, {
      content,
      edited: true,
      editedAt: new Date()
    });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_EDITED",
      messageId,
      description: "Message edited"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_MESSAGE_EDITED",
      actorUserId: user.userId,
      referenceId: messageId,
      message: "Message edited",
      timestamp: new Date().toISOString()
    });

    const media = await this.repository.listMessageMedia(messageId);
    const reactions = await this.repository.listMessageReactions(messageId);
    const response = ChatMapper.toMessageResponse(updated, media, reactions);
    ChatSocketHandler.emitMessageEdited(conversationId, response);
    return response;
  }

  async deleteMessageForMe(user: UserContext, conversationId: string, messageId: string): Promise<void> {
    await this.ensureActiveParticipant(conversationId, user.userId);
    await this.repository.upsertDeleteState(messageId, user.userId);

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_DELETED_FOR_ME",
      messageId,
      description: "Message deleted for me"
    });
  }

  async deleteMessageForEveryone(user: UserContext, conversationId: string, messageId: string): Promise<void> {
    const message = await this.requireMessageOwner(conversationId, messageId, user.userId);
    this.ensureDeletable(message);

    if (message.deletedForEveryone) {
      throw new MessageAlreadyDeletedError();
    }

    await this.repository.updateMessage(messageId, {
      deletedForEveryone: true,
      deletedAt: new Date(),
      content: null
    });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_DELETED_FOR_EVERYONE",
      messageId,
      description: "Message deleted for everyone"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_MESSAGE_DELETED",
      actorUserId: user.userId,
      referenceId: messageId,
      message: "Message deleted for everyone",
      timestamp: new Date().toISOString()
    });

    ChatSocketHandler.emitMessageDeleted(conversationId, messageId, true);
  }

  async markRead(user: UserContext, conversationId: string, messageId: string): Promise<void> {
    await this.ensureActiveParticipant(conversationId, user.userId);
    await this.repository.createReadReceipt({
      message: { connect: { id: messageId } },
      userId: user.userId
    });
    await this.repository.updateParticipant(conversationId, user.userId, {
      lastReadMessageId: messageId,
      lastReadAt: new Date(),
      unreadCount: 0
    });

    ChatSocketHandler.emitMessageRead(conversationId, messageId, user.userId);

    await LoggingClient.logEvent({
      eventType: "CHAT_MESSAGE_READ",
      actorUserId: user.userId,
      referenceId: messageId,
      message: "Message read",
      timestamp: new Date().toISOString()
    });
  }

  async readAll(user: UserContext, conversationId: string): Promise<void> {
    await this.ensureActiveParticipant(conversationId, user.userId);
    await this.repository.updateParticipant(conversationId, user.userId, {
      unreadCount: 0,
      lastReadAt: new Date()
    });
  }

  async addReaction(user: UserContext, conversationId: string, messageId: string, emoji: string): Promise<MessageReactionResponse> {
    await this.ensureActiveParticipant(conversationId, user.userId);
    const reaction = await this.repository.createReaction({
      message: { connect: { id: messageId } },
      userId: user.userId,
      emoji
    });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_REACTED",
      messageId,
      description: "Reaction added"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_REACTION_ADDED",
      actorUserId: user.userId,
      referenceId: messageId,
      message: "Reaction added",
      timestamp: new Date().toISOString()
    });

    const response = ChatMapper.toMessageReactionResponse(reaction);
    ChatSocketHandler.emitReactionAdded(conversationId, response);
    return response;
  }

  async removeReaction(user: UserContext, conversationId: string, messageId: string, emoji: string): Promise<void> {
    await this.ensureActiveParticipant(conversationId, user.userId);
    await this.repository.deleteReaction(messageId, user.userId, emoji);

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_REACTION_REMOVED",
      messageId,
      description: "Reaction removed"
    });

    await LoggingClient.logEvent({
      eventType: "CHAT_REACTION_REMOVED",
      actorUserId: user.userId,
      referenceId: messageId,
      message: "Reaction removed",
      timestamp: new Date().toISOString()
    });

    ChatSocketHandler.emitReactionRemoved(conversationId, messageId, user.userId, emoji);
  }

  async pinMessage(user: UserContext, conversationId: string, messageId: string): Promise<void> {
    await this.requirePinPermission(user.userId, conversationId);
    const count = await this.repository.countPinnedMessages(conversationId);
    if (count >= env.CHAT_MAX_PINNED_MESSAGES) {
      throw new ParticipantPermissionDeniedError("Pinned messages limit reached");
    }

    await this.repository.createPinnedMessage({
      conversation: { connect: { id: conversationId } },
      messageId,
      pinnedByUserId: user.userId
    });

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_PINNED",
      messageId,
      description: "Message pinned"
    });
  }

  async unpinMessage(user: UserContext, conversationId: string, messageId: string): Promise<void> {
    await this.requirePinPermission(user.userId, conversationId);
    await this.repository.deletePinnedMessage(conversationId, messageId);

    await this.repository.createAudit({
      conversationId,
      actorUserId: user.userId,
      action: "MESSAGE_UNPINNED",
      messageId,
      description: "Message unpinned"
    });
  }

  async listPinnedMessages(user: UserContext, conversationId: string) {
    await this.ensureActiveParticipant(conversationId, user.userId);
    return this.repository.listPinnedMessages(conversationId);
  }

  private async ensureActiveParticipant(conversationId: string, userId: number) {
    const participant = await this.repository.findParticipant(conversationId, userId);
    if (!participant) {
      throw new ParticipantNotFoundError();
    }
    if (participant.status !== ParticipantStatus.ACTIVE) {
      throw new ConversationAccessDeniedError();
    }
    return participant;
  }

  private async requireAdmin(userId: number, conversationId: string): Promise<void> {
    const participant = await this.ensureActiveParticipant(conversationId, userId);
    if (participant.role !== ParticipantRole.ADMIN && participant.role !== ParticipantRole.OWNER) {
      throw new GroupAdminRequiredError();
    }
  }

  private async requireOwner(userId: number, conversationId: string): Promise<void> {
    const participant = await this.ensureActiveParticipant(conversationId, userId);
    if (participant.role !== ParticipantRole.OWNER) {
      throw new GroupOwnerRequiredError();
    }
  }

  private async requirePinPermission(userId: number, conversationId: string): Promise<void> {
    const conversation = await this.getConversationOrThrow(conversationId);
    if (conversation.type === "DIRECT") {
      await this.ensureActiveParticipant(conversationId, userId);
      return;
    }
    await this.requireAdmin(userId, conversationId);
  }

  private validateMessageInput(input: { messageType: MessageType; content?: string | null; mediaIds?: number[]; }): void {
    if (input.messageType === "TEXT" && (!input.content || input.content.trim().length === 0)) {
      throw new ParticipantPermissionDeniedError("Content is required for text messages");
    }
    if (["IMAGE", "VIDEO", "AUDIO", "FILE"].includes(input.messageType) && (!input.mediaIds || input.mediaIds.length === 0)) {
      throw new MediaValidationFailedError("Media is required");
    }
    if (input.messageType === "MIXED" && (!input.content && (!input.mediaIds || input.mediaIds.length === 0))) {
      throw new ParticipantPermissionDeniedError("Content or media required for mixed messages");
    }
  }

  private async requireMessageOwner(conversationId: string, messageId: string, userId: number) {
    const message = await this.repository.findMessage(messageId);
    if (!message || message.conversationId !== conversationId) {
      throw new MessageNotFoundError();
    }
    if (message.senderId !== userId) {
      throw new ParticipantPermissionDeniedError("Only sender can modify message");
    }
    return message;
  }

  private ensureEditable(message: { createdAt: Date; edited: boolean; messageType: MessageType; deletedForEveryone: boolean; }) {
    if (message.deletedForEveryone) {
      throw new MessageAlreadyDeletedError();
    }
    if (message.messageType !== "TEXT" && message.messageType !== "MIXED") {
      throw new ParticipantPermissionDeniedError("Message type cannot be edited");
    }
    const diffMinutes = (Date.now() - message.createdAt.getTime()) / 60000;
    if (diffMinutes > env.CHAT_MESSAGE_EDIT_WINDOW_MINUTES) {
      throw new MessageEditWindowExpiredError();
    }
  }

  private ensureDeletable(message: { createdAt: Date; deletedForEveryone: boolean; }) {
    if (message.deletedForEveryone) {
      throw new MessageAlreadyDeletedError();
    }
    const diffMinutes = (Date.now() - message.createdAt.getTime()) / 60000;
    if (diffMinutes > env.CHAT_MESSAGE_DELETE_FOR_EVERYONE_WINDOW_MINUTES) {
      throw new MessageDeleteWindowExpiredError();
    }
  }

  private async getConversationOrThrow(conversationId: string) {
    const conversation = await this.repository.getConversation(conversationId);
    if (!conversation) {
      throw new ConversationNotFoundError();
    }
    return conversation;
  }

  private async getDirectTargetUserId(conversationId: string, userId: number): Promise<number> {
    const participants = await this.repository.findParticipants(conversationId);
    const other = participants.find((p) => p.userId !== userId);
    if (!other) {
      throw new ConversationNotFoundError();
    }
    return other.userId;
  }

  private async incrementUnreadCounts(conversationId: string, senderId: number): Promise<void> {
    const participants = await this.repository.findParticipants(conversationId);
    for (const participant of participants) {
      if (participant.userId === senderId) {
        continue;
      }
      await this.repository.updateParticipant(conversationId, participant.userId, {
        unreadCount: participant.unreadCount + 1
      });
    }
  }

  private async filterValidMentionedUsers(conversationId: string, mentionedUserIds: number[]): Promise<number[]> {
    const participants = await this.repository.findParticipants(conversationId);
    const participantIds = new Set(participants.map((p) => p.userId));
    return Array.from(new Set(mentionedUserIds)).filter((id) => participantIds.has(id));
  }

  private async publishNotifications(conversationId: string, senderId: number, message: MessageResponse, mentionedUserIds: number[]) {
    const participants = await this.repository.findParticipants(conversationId);
    for (const participant of participants) {
      if (participant.userId === senderId) {
        continue;
      }
      const muted = participant.mutedUntil && participant.mutedUntil > new Date();
      if (muted) {
        continue;
      }
      const presenceKey = `presence:user:${participant.userId}`;
      const isOnline = await redis.get(presenceKey);
      if (!isOnline) {
        await NotificationPublisher.publishNewMessageNotification({
          type: "NEW_MESSAGE",
          userId: participant.userId,
          data: { conversationId, message }
        });
      }
    }

    for (const mentionedUserId of mentionedUserIds) {
      await NotificationPublisher.publishMentionNotification({
        type: "MENTION",
        userId: mentionedUserId,
        data: { conversationId, messageId: message.id }
      });
    }
  }

  private async transferOwnershipOnLeave(conversationId: string, leavingOwnerId: number): Promise<void> {
    const participants = await this.repository.findParticipants(conversationId);
    const nextOwner = participants.find((p) => p.userId !== leavingOwnerId && p.status === ParticipantStatus.ACTIVE);
    if (!nextOwner) {
      throw new ParticipantPermissionDeniedError("No participant to transfer ownership");
    }
    await this.repository.updateParticipant(conversationId, leavingOwnerId, { role: ParticipantRole.ADMIN });
    await this.repository.updateParticipant(conversationId, nextOwner.userId, { role: ParticipantRole.OWNER });
  }
}
