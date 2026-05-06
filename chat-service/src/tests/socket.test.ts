import { createServer } from "http";
import { Server } from "socket.io";
import Client from "socket.io-client";
import { initSocket } from "../config/socket";

jest.mock("../clients/auth.client", () => ({
  AuthClient: {
    validateToken: jest.fn().mockResolvedValue({ userId: 1, username: "u", fullName: "User", roles: [] })
  }
}));

jest.mock("../modules/chat/chat.service", () => ({
  ChatService: jest.fn().mockImplementation(() => ({
    sendMessage: jest.fn().mockResolvedValue({ id: "m1", conversationId: "c1" }),
    markRead: jest.fn().mockResolvedValue(undefined)
  }))
}));

let io: Server;
let httpServer: any;
let httpServerAddr: any;

beforeAll((done) => {
  httpServer = createServer();
  io = new Server(httpServer, { cors: { origin: "*" } });
  initSocket(io);
  httpServer.listen(() => {
    httpServerAddr = httpServer.address();
    done();
  });
});

afterAll((done) => {
  io.close();
  httpServer.close(done);
});

test("invalid token connection rejected", (done) => {
  const socket = Client(`http://localhost:${httpServerAddr.port}/chat`, {
    auth: { token: "" },
    reconnection: false
  });
  socket.on("connect_error", () => {
    socket.close();
    done();
  });
});

test("valid token connection accepted", (done) => {
  const socket = Client(`http://localhost:${httpServerAddr.port}/chat`, {
    auth: { token: "Bearer token" },
    reconnection: false
  });
  socket.on("connect", () => {
    socket.close();
    done();
  });
});

test("message:send emits message:new", (done) => {
  const socket = Client(`http://localhost:${httpServerAddr.port}/chat`, {
    auth: { token: "Bearer token" },
    reconnection: false
  });
  socket.on("connect", () => {
    socket.emit("conversation:join", { conversationId: "c1" });
    socket.emit("message:send", {
      conversationId: "c1",
      messageType: "TEXT",
      content: "Hi",
      clientMessageId: "tmp"
    });
    socket.on("message:sent", (payload: any) => {
      expect(payload.message.id).toBe("m1");
      socket.close();
      done();
    });
  });
});

test("typing:start emits typing:start", (done) => {
  const socket = Client(`http://localhost:${httpServerAddr.port}/chat`, {
    auth: { token: "Bearer token" },
    reconnection: false
  });
  socket.on("connect", () => {
    socket.emit("conversation:join", { conversationId: "c1" });
    socket.emit("typing:start", { conversationId: "c1" });
    socket.on("typing:start", () => {
      socket.close();
      done();
    });
  });
});

test("message:read emits message:read", (done) => {
  const socket = Client(`http://localhost:${httpServerAddr.port}/chat`, {
    auth: { token: "Bearer token" },
    reconnection: false
  });
  socket.on("connect", () => {
    socket.emit("conversation:join", { conversationId: "c1" });
    socket.emit("message:read", { conversationId: "c1", messageId: "m1" });
    socket.on("message:read", () => {
      socket.close();
      done();
    });
  });
});
