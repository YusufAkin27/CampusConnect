import axios from "axios";
import { env } from "../config/env";

const client = axios.create({
  baseURL: env.FRIEND_SERVICE_URL,
  timeout: 4000
});

export const FriendClient = {
  async areFriends(userId1: number, userId2: number): Promise<boolean> {
    const response = await client.get("/v1/api/friends/internal/are-friends", {
      params: { userId1, userId2 }
    });
    return Boolean(response.data?.areFriends);
  },

  async isBlockedByEitherSide(userId1: number, userId2: number): Promise<boolean> {
    const response = await client.get("/v1/api/friends/internal/is-blocked", {
      params: { userId1, userId2 }
    });
    return Boolean(response.data?.blocked);
  },

  async canSendDirectMessage(senderId: number, receiverId: number): Promise<boolean> {
    const response = await client.get("/v1/api/friends/internal/can-send-direct", {
      params: { senderId, receiverId }
    });
    return Boolean(response.data?.allowed);
  }
};
