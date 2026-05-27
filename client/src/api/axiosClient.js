import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  getStoredAuthUser,
  setTokens,
} from "utils/tokenStorage";

const API_BASE_URL =
  process.env.REACT_APP_API_BASE_URL ||
  process.env.VITE_API_BASE_URL ||
  "http://localhost:8080";

let refreshPromise = null;

const buildUrl = (url) => {
  if (url.startsWith("http")) return url;
  return `${API_BASE_URL}${url}`;
};

const parseResponse = async (response) => {
  const text = await response.text();
  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
};

const buildError = (response, data) => {
  const validationMessage =
    data?.errors && typeof data.errors === "object"
      ? Object.values(data.errors).join(" ")
      : null;
  const message =
    validationMessage ||
    data?.message ||
    data?.error ||
    (response.status === 403
      ? "Bu işlem için yetkiniz yok."
      : response.status === 404
      ? "Kayıt bulunamadı."
      : response.status === 401
      ? "Oturumunuz sona erdi."
      : "İşlem tamamlanamadı.");

  const error = new Error(message);
  error.status = response.status;
  error.data = data;
  return error;
};

const refreshAccessToken = async () => {
  const refreshToken = getRefreshToken();
  if (!refreshToken) throw new Error("Refresh token bulunamadı.");

  if (!refreshPromise) {
    refreshPromise = fetch(buildUrl("/v1/api/auth/refresh-token"), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    })
      .then(async (response) => {
        const data = await parseResponse(response);
        if (!response.ok) throw buildError(response, data);
        setTokens(data);
        return data.accessToken;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
};

export const apiRequest = async (url, options = {}, retry = true) => {
  const token = getAccessToken();
  const authUser = getStoredAuthUser();
  const headers = new Headers(options.headers || {});

  if (!headers.has("Content-Type") && options.body && !(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  if (token) headers.set("Authorization", `Bearer ${token}`);
  if (authUser?.id) {
    headers.set("X-Auth-User-Id", String(authUser.id));
    headers.set("X-User-Id", String(authUser.id));
  }

  const response = await fetch(buildUrl(url), {
    ...options,
    headers,
  });
  const data = await parseResponse(response);

  if (response.status === 401 && retry && getRefreshToken()) {
    try {
      await refreshAccessToken();
      return apiRequest(url, options, false);
    } catch (error) {
      clearTokens();
      window.location.assign("/");
      throw error;
    }
  }

  if (!response.ok) throw buildError(response, data);
  return data;
};

export const unwrapData = (response) => response?.data ?? response;

export const unwrapPageContent = (response) => {
  const data = unwrapData(response);
  return data?.content ?? [];
};
