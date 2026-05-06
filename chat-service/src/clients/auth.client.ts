import axios from "axios";
import { env } from "../config/env";
import { UnauthorizedError } from "../errors/chat.errors";

export type AuthUserContext = {
  userId: number;
  username: string;
  fullName: string;
  roles: string[];
  token: string | null;
};

export type AuthUserResponse = {
  userId: number;
  username: string;
  fullName: string;
  roles: string[];
};

const client = axios.create({
  baseURL: env.AUTH_SERVICE_URL,
  timeout: 4000
});

export const AuthClient = {
  async validateToken(accessToken: string): Promise<AuthUserResponse> {
    const response = await client.post("/v1/api/auth/internal/validate", {
      token: accessToken
    });
    return response.data;
  },

  async introspectToken(accessToken: string): Promise<AuthUserResponse> {
    const response = await client.post("/v1/api/auth/internal/introspect", {
      token: accessToken
    });
    return response.data;
  },

  getCurrentUserFromHeaders(headers: Record<string, string | string[] | undefined>): AuthUserContext {
    const userIdHeader = headers["x-user-id"] as string | undefined;
    const username = headers["x-username"] as string | undefined;
    const fullName = headers["x-full-name"] as string | undefined;
    const rolesHeader = headers["x-user-roles"] as string | undefined;

    if (userIdHeader) {
      const roles = rolesHeader ? rolesHeader.split(",").map((r) => r.trim()).filter(Boolean) : [];
      return {
        userId: Number(userIdHeader),
        username: username || "",
        fullName: fullName || "",
        roles,
        token: null
      };
    }

    const authHeader = headers.authorization as string | undefined;
    if (!authHeader) {
      throw new UnauthorizedError("Authorization header missing");
    }
    return {
      userId: 0,
      username: "",
      fullName: "",
      roles: [],
      token: authHeader
    };
  }
};
