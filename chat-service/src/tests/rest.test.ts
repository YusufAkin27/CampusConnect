import request from "supertest";
import { createApp } from "../app";

jest.mock("../modules/chat/chat.service", () => {
  return {
    ChatService: jest.fn().mockImplementation(() => ({
      createDirectConversation: jest.fn().mockResolvedValue({ id: "c1", type: "DIRECT" }),
      createGroupConversation: jest.fn().mockResolvedValue({ id: "c2", type: "GROUP" }),
      sendMessage: jest.fn().mockResolvedValue({ id: "m1", conversationId: "c1" }),
      listMessages: jest.fn().mockResolvedValue({ data: [], total: 0 }),
      addMembers: jest.fn().mockResolvedValue(undefined),
      removeMember: jest.fn().mockResolvedValue(undefined),
      getConversationDetail: jest.fn().mockResolvedValue({ id: "c1" }),
      listConversations: jest.fn().mockResolvedValue({ data: [], total: 0 }),
      updateGroup: jest.fn().mockResolvedValue({ id: "c2" }),
      leaveConversation: jest.fn().mockResolvedValue(undefined),
      archiveConversation: jest.fn().mockResolvedValue(undefined),
      pinConversation: jest.fn().mockResolvedValue(undefined),
      muteConversation: jest.fn().mockResolvedValue(undefined),
      changeRole: jest.fn().mockResolvedValue(undefined),
      transferOwnership: jest.fn().mockResolvedValue(undefined),
      editMessage: jest.fn().mockResolvedValue({ id: "m1" }),
      deleteMessageForMe: jest.fn().mockResolvedValue(undefined),
      deleteMessageForEveryone: jest.fn().mockResolvedValue(undefined),
      markRead: jest.fn().mockResolvedValue(undefined),
      readAll: jest.fn().mockResolvedValue(undefined),
      addReaction: jest.fn().mockResolvedValue({ messageId: "m1", emoji: "❤️" }),
      removeReaction: jest.fn().mockResolvedValue(undefined),
      pinMessage: jest.fn().mockResolvedValue(undefined),
      unpinMessage: jest.fn().mockResolvedValue(undefined),
      listPinnedMessages: jest.fn().mockResolvedValue([])
    }))
  };
});

jest.mock("../middlewares/auth.middleware", () => ({
  authMiddleware: (req: any, res: any, next: any) => {
    req.user = { userId: 1, username: "u", fullName: "User", roles: [] };
    next();
  }
}));

const app = createApp();

describe("REST endpoints", () => {
  test("create direct chat", async () => {
    await request(app)
      .post("/v1/api/chats/direct")
      .send({ targetUserId: 2 })
      .expect(200);
  });

  test("create group chat", async () => {
    await request(app)
      .post("/v1/api/chats/groups")
      .send({ title: "Group", memberIds: [2, 3] })
      .expect(200);
  });

  test("send message", async () => {
    await request(app)
      .post("/v1/api/chats/c1/messages")
      .send({ messageType: "TEXT", content: "Hi" })
      .expect(200);
  });

  test("get messages", async () => {
    await request(app)
      .get("/v1/api/chats/c1/messages")
      .expect(200);
  });

  test("add group member", async () => {
    await request(app)
      .post("/v1/api/chats/c1/members")
      .send({ userIds: [5] })
      .expect(200);
  });

  test("remove group member", async () => {
    await request(app)
      .delete("/v1/api/chats/c1/members/5")
      .expect(200);
  });
});
