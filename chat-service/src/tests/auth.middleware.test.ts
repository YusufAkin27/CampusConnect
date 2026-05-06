import { authMiddleware } from "../middlewares/auth.middleware";
import { AuthClient } from "../clients/auth.client";

jest.mock("../clients/auth.client");

describe("auth middleware", () => {
  test("uses header user context", async () => {
    const req: any = { headers: { "x-user-id": "1", "x-username": "u", "x-full-name": "User" } };
    const res: any = {};
    const next = jest.fn();

    await authMiddleware(req, res, next);
    expect(req.user.userId).toBe(1);
  });

  test("validates token", async () => {
    (AuthClient.getCurrentUserFromHeaders as jest.Mock).mockReturnValue({
      userId: 0,
      username: "",
      fullName: "",
      roles: [],
      token: "Bearer token"
    });
    (AuthClient.validateToken as jest.Mock).mockResolvedValue({
      userId: 1,
      username: "u",
      fullName: "User",
      roles: []
    });

    const req: any = { headers: { authorization: "Bearer token" } };
    const res: any = {};
    const next = jest.fn();

    await authMiddleware(req, res, next);
    expect(req.user.userId).toBe(1);
  });
});
