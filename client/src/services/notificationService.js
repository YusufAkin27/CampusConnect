import { apiRequest, unwrapData } from "api/axiosClient";

export const getNotificationPreferences = async () =>
  unwrapData(await apiRequest("/v1/api/notifications/me/preferences"));

export const updateNotificationPreferences = async (preferences) =>
  unwrapData(
    await apiRequest("/v1/api/notifications/me/preferences", {
      method: "PUT",
      body: JSON.stringify(preferences),
    })
  );
