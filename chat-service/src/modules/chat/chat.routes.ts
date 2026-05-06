import { Router } from "express";
import { asyncHandler } from "../../utils/async-handler";
import { authMiddleware } from "../../middlewares/auth.middleware";
import { rateLimitMiddleware } from "../../middlewares/rate-limit.middleware";
import { ChatController } from "./chat.controller";

export const chatRoutes = Router();
const controller = new ChatController();

chatRoutes.use(authMiddleware);

chatRoutes.post("/direct", asyncHandler(controller.createDirect));
chatRoutes.post("/groups", asyncHandler(controller.createGroup));
chatRoutes.get("/", asyncHandler(controller.listConversations));
chatRoutes.get("/:conversationId", asyncHandler(controller.getConversation));
chatRoutes.put("/:conversationId/group", asyncHandler(controller.updateGroup));
chatRoutes.delete("/:conversationId/leave", asyncHandler(controller.leaveConversation));
chatRoutes.post("/:conversationId/archive", asyncHandler(controller.archiveConversation));
chatRoutes.post("/:conversationId/unarchive", asyncHandler(controller.unarchiveConversation));
chatRoutes.post("/:conversationId/pin", asyncHandler(controller.pinConversation));
chatRoutes.delete("/:conversationId/pin", asyncHandler(controller.unpinConversation));
chatRoutes.post("/:conversationId/mute", asyncHandler(controller.muteConversation));
chatRoutes.post("/:conversationId/unmute", asyncHandler(controller.unmuteConversation));

chatRoutes.get("/:conversationId/members", asyncHandler(controller.listMembers));
chatRoutes.post("/:conversationId/members", asyncHandler(controller.addMembers));
chatRoutes.delete("/:conversationId/members/:userId", asyncHandler(controller.removeMember));
chatRoutes.put("/:conversationId/members/:userId/role", asyncHandler(controller.changeRole));
chatRoutes.post("/:conversationId/transfer-ownership", asyncHandler(controller.transferOwnership));

chatRoutes.post("/:conversationId/messages", rateLimitMiddleware, asyncHandler(controller.sendMessage));
chatRoutes.get("/:conversationId/messages", asyncHandler(controller.listMessages));
chatRoutes.put("/:conversationId/messages/:messageId", asyncHandler(controller.editMessage));
chatRoutes.delete("/:conversationId/messages/:messageId/for-me", asyncHandler(controller.deleteForMe));
chatRoutes.delete("/:conversationId/messages/:messageId/for-everyone", asyncHandler(controller.deleteForEveryone));
chatRoutes.post("/:conversationId/messages/:messageId/read", asyncHandler(controller.markRead));
chatRoutes.post("/:conversationId/read-all", asyncHandler(controller.readAll));

chatRoutes.post("/:conversationId/messages/:messageId/reactions", asyncHandler(controller.addReaction));
chatRoutes.delete("/:conversationId/messages/:messageId/reactions", asyncHandler(controller.removeReaction));

chatRoutes.post("/:conversationId/messages/:messageId/pin", asyncHandler(controller.pinMessage));
chatRoutes.delete("/:conversationId/messages/:messageId/pin", asyncHandler(controller.unpinMessage));
chatRoutes.get("/:conversationId/pinned-messages", asyncHandler(controller.listPinned));
