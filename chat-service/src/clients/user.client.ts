import axios from "axios";
import { env } from "../config/env";

export type UserSummary = {
  id: number;
  username: string;
  fullName: string;
  profilePhotoUrl?: string | null;
  department?: string | null;
  faculty?: string | null;
  grade?: string | null;
  isActive: boolean;
};

const client = axios.create({
  baseURL: env.USER_SERVICE_URL,
  timeout: 4000
});

export const UserClient = {
  async getUserById(userId: number): Promise<UserSummary> {
    const response = await client.get(`/v1/api/users/${userId}`);
    return response.data;
  },

  async getUsersByIds(userIds: number[]): Promise<UserSummary[]> {
    const response = await client.post("/v1/api/users/internal/bulk", { userIds });
    return response.data;
  },

  async searchUsers(keyword: string): Promise<UserSummary[]> {
    const response = await client.get("/v1/api/users/search", { params: { keyword } });
    return response.data;
  },

  async existsUser(userId: number): Promise<boolean> {
    const response = await client.get(`/v1/api/users/${userId}/exists`);
    return Boolean(response.data?.exists);
  }
};
