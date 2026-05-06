import { Server } from "socket.io";
import { ChatSocketHandler } from "../modules/chat/chat.socket";
import { authSocketMiddleware } from "../middlewares/auth.middleware";

export function initSocket(io: Server): void {
  const nsp = io.of("/chat");
  nsp.use(authSocketMiddleware);
  nsp.on("connection", (socket) => {
    ChatSocketHandler.register(socket, nsp);
  });
}
