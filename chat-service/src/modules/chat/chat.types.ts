export type UserContext = {
  userId: number;
  username: string;
  fullName: string;
  roles: string[];
  token?: string | null;
};

export type ConversationListItemResponse = {
  id: string;
  type: string;
  title: string | null;
  description: string | null;
  photoUrlSnapshot: string | null;
  lastMessageTextSnapshot: string | null;
  lastMessageAt: string | null;
  unreadCount: number;
  pinned: boolean;
  archived: boolean;
};

export type ConversationDetailResponse = {
  id: string;
  type: string;
  title: string | null;
  description: string | null;
  photoUrlSnapshot: string | null;
  createdByUserId: number;
  isActive: boolean;
  participants: ConversationMemberResponse[];
  createdAt: string;
  updatedAt: string;
};

export type ConversationMemberResponse = {
  userId: number;
  role: string;
  status: string;
  joinedAt: string;
  leftAt: string | null;
  username: string | null;
  fullName: string | null;
  profilePhoto: string | null;
};

export type MessageResponse = {
  id: string;
  conversationId: string;
  senderId: number;
  messageType: string;
  content: string | null;
  replyToMessageId: string | null;
  status: string;
  edited: boolean;
  editedAt: string | null;
  deletedForEveryone: boolean;
  deletedAt: string | null;
  createdAt: string;
  updatedAt: string;
  media: MessageMediaResponse[];
  reactions: MessageReactionResponse[];
};

export type MessageMediaResponse = {
  mediaId: number;
  url: string;
  mediaType: string;
  fileName: string | null;
  size: number | null;
  thumbnailUrl: string | null;
  orderIndex: number;
};

export type MessageReactionResponse = {
  messageId: string;
  userId: number;
  emoji: string;
  createdAt: string;
};
