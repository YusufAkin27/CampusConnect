import { NextFunction, Request, Response } from "express";
import { Socket } from "socket.io";
import { AuthClient, AuthUserContext } from "../clients/auth.client";
import { ExternalServiceUnavailableError, UnauthorizedError } from "../errors/chat.errors";
import { logger } from "../utils/logger";

export async function authMiddleware(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const context = AuthClient.getCurrentUserFromHeaders(req.headers as Record<string, string | string[] | undefined>);
    if (context.token) {
      const token = normalizeToken(context.token);
      const user = await AuthClient.validateToken(token);
      (req as any).user = { ...user, token } as AuthUserContext;
    } else {
      (req as any).user = context as AuthUserContext;
    }
    next();
  } catch (error) {
    if (error instanceof UnauthorizedError) {
      next(error);
      return;
    }
    next(new ExternalServiceUnavailableError("Auth service unavailable"));
  }
}

export async function authSocketMiddleware(socket: Socket, next: (err?: Error) => void): Promise<void> {
  try {
    const tokenHeader = socket.handshake.auth?.token as string | undefined;
    if (!tokenHeader) {
      next(new UnauthorizedError("Socket token missing"));
      return;
    }
    const token = normalizeToken(tokenHeader);
    const user = await AuthClient.validateToken(token);
    socket.data.user = {
      userId: user.userId,
      username: user.username,
      fullName: user.fullName,
      roles: user.roles,
      token
    } as AuthUserContext;
    next();
  } catch (error) {
    logger.warn("Socket auth failed", { error });
    next(new UnauthorizedError("Invalid token"));
  }
}

function normalizeToken(token: string): string {
  if (token.startsWith("Bearer ")) {
    return token.slice(7).trim();
  }
  return token.trim();
}
