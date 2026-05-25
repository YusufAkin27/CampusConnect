import { apiRequest, unwrapData } from "api/axiosClient";

export const getMyProfile = async () => unwrapData(await apiRequest("/v1/api/users/me"));

export const getProfileById = async (id) => unwrapData(await apiRequest(`/v1/api/users/${id}`));

export const getProfileByAuthUserId = async (authUserId) =>
  unwrapData(await apiRequest(`/v1/api/users/auth/${authUserId}`));

export const updateMyProfile = async (profile) =>
  unwrapData(
    await apiRequest("/v1/api/users/me", {
      method: "PUT",
      body: JSON.stringify(profile),
    })
  );

export const updateProfileImage = async (profileImageUrl) =>
  unwrapData(
    await apiRequest("/v1/api/users/me/profile-image", {
      method: "PATCH",
      body: JSON.stringify({ profileImageUrl }),
    })
  );
