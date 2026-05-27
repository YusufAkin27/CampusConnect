import { apiRequest, unwrapData, unwrapPageContent } from "api/axiosClient";

export const getMyFriends = async () =>
  unwrapPageContent(await apiRequest("/v1/api/friends/me?page=0&size=20"));

export const getUserFriends = async (authUserId) =>
  unwrapPageContent(await apiRequest(`/v1/api/friends/user/${authUserId}?page=0&size=20`));

export const sendFriendRequest = async (receiverAuthUserId, message = "") =>
  unwrapData(
    await apiRequest("/v1/api/friends/requests", {
      method: "POST",
      body: JSON.stringify({ receiverAuthUserId, message }),
    })
  );

export const removeFriend = (friendAuthUserId) =>
  apiRequest(`/v1/api/friends/${friendAuthUserId}`, { method: "DELETE" });
