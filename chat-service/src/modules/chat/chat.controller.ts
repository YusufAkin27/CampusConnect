import { Request, Response } from "express";
import { MessageType } from "@prisma/client";
import { successResponse } from "../../utils/api-response";
import { toPageResponse } from "../../utils/pagination";
import { ChatService } from "./chat.service";
import {
  addMembersSchema,
  createDirectSchema,
  createGroupSchema,
  messageCreateSchema,
  messageEditSchema,
  muteSchema,
  reactionSchema,
  roleSchema,
  transferOwnershipSchema,
  updateGroupSchema
} from "./chat.validator";

export class ChatController {
  private service: ChatService;

  constructor(service: ChatService = new ChatService()) {
    this.service = service;
  }

  createDirect = async (req: Request, res: Response) => {
    const data = createDirectSchema.parse(req.body);
    const user = (req as any).user;
    const result = await this.service.createDirectConversation(user, data.targetUserId);
    res.json(successResponse("Direct conversation created", result));
  };

  createGroup = async (req: Request, res: Response) => {
    const data = createGroupSchema.parse(req.body);
    const user = (req as any).user;
    const result = await this.service.createGroupConversation(user, data);
    res.json(successResponse("Group created", result));
  };

  listConversations = async (req: Request, res: Response) => {
    const user = (req as any).user;
    const page = Number(req.query.page ?? 0);
    const size = Number(req.query.size ?? 20);
    const type = req.query.type as string | undefined;
    const archived = req.query.archived !== undefined ? req.query.archived === "true" : undefined;
    const pinned = req.query.pinned !== undefined ? req.query.pinned === "true" : undefined;

    const { data, total } = await this.service.listConversations(user, { page, size, type, archived, pinned });
    res.json(successResponse("Conversations fetched", toPageResponse(data, page, size, total)));
  };

  getConversation = async (req: Request, res: Response) => {
    const user = (req as any).user;
    const result = await this.service.getConversationDetail(user, req.params.conversationId);
    res.json(successResponse("Conversation fetched", result));
  };

  updateGroup = async (req: Request, res: Response) => {
    const data = updateGroupSchema.parse(req.body);
    const user = (req as any).user;
    const result = await this.service.updateGroup(user, req.params.conversationId, data);
    res.json(successResponse("Group updated", result));
  };

  leaveConversation = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.leaveConversation(user, req.params.conversationId);
    res.json(successResponse("Conversation left", {}));
  };

  archiveConversation = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.archiveConversation(user, req.params.conversationId, true);
    res.json(successResponse("Conversation archived", {}));
  };

  unarchiveConversation = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.archiveConversation(user, req.params.conversationId, false);
    res.json(successResponse("Conversation unarchived", {}));
  };

  pinConversation = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.pinConversation(user, req.params.conversationId, true);
    res.json(successResponse("Conversation pinned", {}));
  };

  unpinConversation = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.pinConversation(user, req.params.conversationId, false);
    res.json(successResponse("Conversation unpinned", {}));
  };

  muteConversation = async (req: Request, res: Response) => {
    const data = muteSchema.parse(req.body);
    const user = (req as any).user;
    await this.service.muteConversation(user, req.params.conversationId, data.mutedUntil);
    res.json(successResponse("Conversation muted", {}));
  };

  unmuteConversation = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.muteConversation(user, req.params.conversationId, null);
    res.json(successResponse("Conversation unmuted", {}));
  };

  listMembers = async (req: Request, res: Response) => {
    const user = (req as any).user;
    const members = await this.service.listMembers(user, req.params.conversationId);
    res.json(successResponse("Members fetched", members));
  };

  addMembers = async (req: Request, res: Response) => {
    const data = addMembersSchema.parse(req.body);
    const user = (req as any).user;
    await this.service.addMembers(user, req.params.conversationId, data.userIds);
    res.json(successResponse("Members added", {}));
  };

  removeMember = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.removeMember(user, req.params.conversationId, Number(req.params.userId));
    res.json(successResponse("Member removed", {}));
  };

  changeRole = async (req: Request, res: Response) => {
    const data = roleSchema.parse(req.body);
    const user = (req as any).user;
    await this.service.changeRole(user, req.params.conversationId, Number(req.params.userId), data.role);
    res.json(successResponse("Role updated", {}));
  };

  transferOwnership = async (req: Request, res: Response) => {
    const data = transferOwnershipSchema.parse(req.body);
    const user = (req as any).user;
    await this.service.transferOwnership(user, req.params.conversationId, data.newOwnerUserId);
    res.json(successResponse("Ownership transferred", {}));
  };

  sendMessage = async (req: Request, res: Response) => {
    const data = messageCreateSchema.parse(req.body);
    const user = (req as any).user;
    const result = await this.service.sendMessage(user, req.params.conversationId, {
      messageType: data.messageType as MessageType,
      content: data.content ?? null,
      mediaIds: data.mediaIds ?? [],
      replyToMessageId: data.replyToMessageId ?? null,
      mentionedUserIds: data.mentionedUserIds ?? []
    });
    res.json(successResponse("Message sent", result));
  };

  listMessages = async (req: Request, res: Response) => {
    const user = (req as any).user;
    const page = Number(req.query.page ?? 0);
    const size = Number(req.query.size ?? 20);
    const result = await this.service.listMessages(user, req.params.conversationId, page, size);
    res.json(successResponse("Messages fetched", toPageResponse(result.data, page, size, result.total)));
  };

  editMessage = async (req: Request, res: Response) => {
    const data = messageEditSchema.parse(req.body);
    const user = (req as any).user;
    const result = await this.service.editMessage(user, req.params.conversationId, req.params.messageId, data.content);
    res.json(successResponse("Message edited", result));
  };

  deleteForMe = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.deleteMessageForMe(user, req.params.conversationId, req.params.messageId);
    res.json(successResponse("Message deleted for me", {}));
  };

  deleteForEveryone = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.deleteMessageForEveryone(user, req.params.conversationId, req.params.messageId);
    res.json(successResponse("Message deleted", {}));
  };

  markRead = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.markRead(user, req.params.conversationId, req.params.messageId);
    res.json(successResponse("Message read", {}));
  };

  readAll = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.readAll(user, req.params.conversationId);
    res.json(successResponse("Conversation read", {}));
  };

  addReaction = async (req: Request, res: Response) => {
    const data = reactionSchema.parse(req.body);
    const user = (req as any).user;
    const result = await this.service.addReaction(user, req.params.conversationId, req.params.messageId, data.emoji);
    res.json(successResponse("Reaction added", result));
  };

  removeReaction = async (req: Request, res: Response) => {
    const data = reactionSchema.parse(req.body);
    const user = (req as any).user;
    await this.service.removeReaction(user, req.params.conversationId, req.params.messageId, data.emoji);
    res.json(successResponse("Reaction removed", {}));
  };

  pinMessage = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.pinMessage(user, req.params.conversationId, req.params.messageId);
    res.json(successResponse("Message pinned", {}));
  };

  unpinMessage = async (req: Request, res: Response) => {
    const user = (req as any).user;
    await this.service.unpinMessage(user, req.params.conversationId, req.params.messageId);
    res.json(successResponse("Message unpinned", {}));
  };

  listPinned = async (req: Request, res: Response) => {
    const user = (req as any).user;
    const result = await this.service.listPinnedMessages(user, req.params.conversationId);
    res.json(successResponse("Pinned messages fetched", result));
  };
}
