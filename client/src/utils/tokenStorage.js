const ACCESS_TOKEN_KEY = "campusConnectAccessToken";
const REFRESH_TOKEN_KEY = "campusConnectRefreshToken";
const AUTH_USER_KEY = "campusConnectAuthUser";

export const getAccessToken = () => localStorage.getItem(ACCESS_TOKEN_KEY);

export const getRefreshToken = () => localStorage.getItem(REFRESH_TOKEN_KEY);

export const getStoredAuthUser = () => {
  const rawUser = localStorage.getItem(AUTH_USER_KEY);
  if (!rawUser) return null;

  try {
    return JSON.parse(rawUser);
  } catch {
    return null;
  }
};

export const setTokens = ({ accessToken, refreshToken, user }) => {
  if (accessToken) localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  if (refreshToken) localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  if (user) localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
};

export const clearTokens = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(AUTH_USER_KEY);
};

export const isAuthenticated = () => Boolean(getAccessToken());
