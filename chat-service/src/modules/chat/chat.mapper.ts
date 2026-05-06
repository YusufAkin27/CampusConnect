import { Conversation, ConversationParticipant, Message, MessageMedia, MessageReaction } from "@prisma/client";
import { ConversationDetailResponse, ConversationListItemResponse, MessageMediaResponse, MessageReactionResponse, MessageResponse } from "./chat.types";

export const ChatMapper = {
  toConversationListItem(participant: ConversationParticipant, conversation: Conversation): ConversationListItemResponse {
    return {
      id: conversation.id,
      type: conversation.type,
      title: conversation.title,
      description: conversation.description,
      photoUrlSnapshot: conversation.photoUrlSnapshot,
      lastMessageTextSnapshot: conversation.lastMessageTextSnapshot,
      lastMessageAt: conversation.lastMessageAt ? conversation.lastMessageAt.toISOString() : null,
      unreadCount: participant.unreadCount,
      pinned: participant.pinned,
      archived: participant.archived
    };
  },

  toConversationDetail(conversation: Conversation, participants: ConversationParticipant[]): ConversationDetailResponse {
    return {
      id: conversation.id,
      type: conversation.type,
      title: conversation.title,
      description: conversation.description,
      photoUrlSnapshot: conversation.photoUrlSnapshot,
      createdByUserId: conversation.createdByUserId,
      isActive: conversation.isActive,
      participants: participants.map((p) => ({
        userId: p.userId,
        role: p.role,
        status: p.status,
        joinedAt: p.joinedAt.toISOString(),
        leftAt: p.leftAt ? p.leftAt.toISOString() : null,
        username: p.usernameSnapshot,
        fullName: p.fullNameSnapshot,
        profilePhoto: p.profilePhotoSnapshot
      })),
      createdAt: conversation.createdAt.toISOString(),
      updatedAt: conversation.updatedAt.toISOString()
    };
  },

  toMessageResponse(message: Message, media: MessageMedia[], reactions: MessageReaction[]): MessageResponse {
    return {
      id: message.id,
      conversationId: message.conversationId,
      senderId: message.senderId,
      messageType: message.messageType,
      content: message.deletedForEveryone ? null : message.content,
      replyToMessageId: message.replyToMessageId,
      status: message.status,
      edited: message.edited,
      editedAt: message.editedAt ? message.editedAt.toISOString() : null,
      deletedForEveryone: message.deletedForEveryone,
      deletedAt: message.deletedAt ? message.deletedAt.toISOString() : null,
      createdAt: message.createdAt.toISOString(),
      updatedAt: message.updatedAt.toISOString(),
      media: message.deletedForEveryone ? [] : media.map(ChatMapper.toMessageMediaResponse),
      reactions: reactions.map(ChatMapper.toMessageReactionResponse)
    };
  },

  toMessageMediaResponse(media: MessageMedia): MessageMediaResponse {
    return {
      mediaId: media.mediaId,
      url: media.mediaUrlSnapshot,
      mediaType: media.mediaType,
      fileName: media.fileName,
      size: media.size,
      thumbnailUrl: media.thumbnailUrl,
      orderIndex: media.orderIndex
    };
  },

  toMessageReactionResponse(reaction: MessageReaction): MessageReactionResponse {
    return {
      messageId: reaction.messageId,
      userId: reaction.userId,
      emoji: reaction.emoji,
      createdAt: reaction.createdAt.toISOString()
    };
  }
};
