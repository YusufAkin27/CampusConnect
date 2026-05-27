import { apiRequest } from "api/axiosClient";
import { clearTokens, getRefreshToken, setTokens } from "utils/tokenStorage";

export const login = async ({ usernameOrEmail, password }) => {
  const response = await apiRequest("/v1/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ usernameOrEmail, password }),
  });
  setTokens(response);
  return response;
};

export const register = ({ username, email, password }) =>
  apiRequest("/v1/api/auth/register", {
    method: "POST",
    body: JSON.stringify({ username, email, password }),
  });

export const logout = async () => {
  const refreshToken = getRefreshToken();
  if (refreshToken) {
    try {
      await apiRequest("/v1/api/auth/logout", {
        method: "POST",
        body: JSON.stringify({ refreshToken }),
      });
    } catch {
      // Logout should still clear the local session when the server rejects the token.
    }
  }
  clearTokens();
};

export const getCurrentAuthUser = () => apiRequest("/v1/api/auth/me");

export const changePassword = ({ currentPassword, newPassword, confirmPassword }) =>
  apiRequest("/v1/api/auth/change-password", {
    method: "POST",
    body: JSON.stringify({ currentPassword, newPassword, confirmPassword }),
  });
