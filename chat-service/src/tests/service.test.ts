import { MessageType, ParticipantRole, ParticipantStatus } from "@prisma/client";
import { ChatService } from "../modules/chat/chat.service";
import { ParticipantPermissionDeniedError, UserBlockedError } from "../errors/chat.errors";
import { FriendClient } from "../clients/friend.client";
import { UserClient } from "../clients/user.client";

jest.mock("../clients/friend.client");
jest.mock("../clients/user.client");

const mockRepo = {
  findDirectConversation: jest.fn(),
  createConversation: jest.fn(),
  findParticipants: jest.fn(),
  createAudit: jest.fn(),
  listUserParticipants: jest.fn(),
  countUserParticipants: jest.fn(),
  getConversation: jest.fn(),
  findParticipant: jest.fn(),
  updateConversation: jest.fn(),
  createMessage: jest.fn(),
  listMessageMedia: jest.fn(),
  listMessageReactions: jest.fn(),
  updateMessage: jest.fn(),
  findMessage: jest.fn(),
  createReadReceipt: jest.fn(),
  updateParticipant: jest.fn(),
  createReaction: jest.fn(),
  deleteReaction: jest.fn()
};

const user = { userId: 1, username: "u", fullName: "User", roles: [] };

describe("ChatService", () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  test("direct conversation create", async () => {
    (UserClient.existsUser as jest.Mock).mockResolvedValue(true);
    (FriendClient.isBlockedByEitherSide as jest.Mock).mockResolvedValue(false);
    mockRepo.findDirectConversation.mockResolvedValue(null);
    mockRepo.createConversation.mockResolvedValue({ id: "c1", type: "DIRECT", createdByUserId: 1 });
    mockRepo.findParticipants.mockResolvedValue([]);

    const service = new ChatService(mockRepo as any);
    await service.createDirectConversation(user, 2);

    expect(mockRepo.createConversation).toHaveBeenCalled();
  });

  test("duplicate direct conversation", async () => {
    (UserClient.existsUser as jest.Mock).mockResolvedValue(true);
    (FriendClient.isBlockedByEitherSide as jest.Mock).mockResolvedValue(false);
    mockRepo.findDirectConversation.mockResolvedValue({ id: "c1", type: "DIRECT" });
    mockRepo.findParticipants.mockResolvedValue([]);

    const service = new ChatService(mockRepo as any);
    await service.createDirectConversation(user, 2);

    expect(mockRepo.createConversation).not.toHaveBeenCalled();
  });

  test("group create validation", async () => {
    const service = new ChatService(mockRepo as any);
    await expect(service.createGroupConversation(user, {
      title: "Group",
      memberIds: [2],
      description: null,
      photoMediaId: null
    })).rejects.toBeInstanceOf(ParticipantPermissionDeniedError);
  });

  test("group member permission", async () => {
    mockRepo.findParticipant.mockResolvedValue({ role: ParticipantRole.MEMBER, status: ParticipantStatus.ACTIVE });
    const service = new ChatService(mockRepo as any);
    await expect(service.removeMember(user, "c1", 2)).rejects.toBeInstanceOf(ParticipantPermissionDeniedError);
  });

  test("message send validation", async () => {
    mockRepo.findParticipant.mockResolvedValue({ role: ParticipantRole.MEMBER, status: ParticipantStatus.ACTIVE });
    mockRepo.getConversation.mockResolvedValue({ id: "c1", type: "GROUP" });

    const service = new ChatService(mockRepo as any);
    await expect(service.sendMessage(user, "c1", {
      messageType: MessageType.TEXT,
      content: "",
      mediaIds: []
    })).rejects.toBeInstanceOf(ParticipantPermissionDeniedError);
  });

  test("message edit permission", async () => {
    mockRepo.findMessage.mockResolvedValue({ id: "m1", conversationId: "c1", senderId: 2 });
    const service = new ChatService(mockRepo as any);
    await expect(service.editMessage(user, "c1", "m1", "hi"))
      .rejects.toBeInstanceOf(ParticipantPermissionDeniedError);
  });

  test("message delete window", async () => {
    const oldDate = new Date(Date.now() - 60 * 60 * 1000);
    mockRepo.findMessage.mockResolvedValue({ id: "m1", conversationId: "c1", senderId: 1, createdAt: oldDate, deletedForEveryone: false });
    const service = new ChatService(mockRepo as any);
    await expect(service.deleteMessageForEveryone(user, "c1", "m1")).rejects.toBeTruthy();
  });

  test("reaction duplicate", async () => {
    mockRepo.findParticipant.mockResolvedValue({ role: ParticipantRole.MEMBER, status: ParticipantStatus.ACTIVE });
    mockRepo.createReaction.mockRejectedValue(new Error("Duplicate"));
    const service = new ChatService(mockRepo as any);
    await expect(service.addReaction(user, "c1", "m1", "❤️")).rejects.toBeTruthy();
  });

  test("read receipt", async () => {
    mockRepo.findParticipant.mockResolvedValue({ role: ParticipantRole.MEMBER, status: ParticipantStatus.ACTIVE });
    const service = new ChatService(mockRepo as any);
    await service.markRead(user, "c1", "m1");
    expect(mockRepo.createReadReceipt).toHaveBeenCalled();
  });

  test("block control", async () => {
    mockRepo.findParticipant.mockResolvedValue({ role: ParticipantRole.MEMBER, status: ParticipantStatus.ACTIVE });
    mockRepo.getConversation.mockResolvedValue({ id: "c1", type: "DIRECT" });
    mockRepo.findParticipants.mockResolvedValue([{ userId: 1 }, { userId: 2 }]);
    (FriendClient.isBlockedByEitherSide as jest.Mock).mockResolvedValue(true);

    const service = new ChatService(mockRepo as any);
    await expect(service.sendMessage(user, "c1", {
      messageType: MessageType.TEXT,
      content: "hi",
      mediaIds: []
    })).rejects.toBeInstanceOf(UserBlockedError);
  });
});
