import { apiRequest, unwrapData, unwrapPageContent } from "api/axiosClient";

export const getFeed = async () =>
  unwrapPageContent(await apiRequest("/v1/api/posts/feed?page=0&size=20&sortType=NEWEST"));

export const getMyPosts = async () =>
  unwrapPageContent(await apiRequest("/v1/api/posts/me?page=0&size=20"));

export const getUserPosts = async (authUserId) =>
  unwrapPageContent(await apiRequest(`/v1/api/posts/user/${authUserId}?page=0&size=20`));

export const createPost = async ({ content, mediaList = [] }) =>
  unwrapData(
    await apiRequest("/v1/api/posts", {
      method: "POST",
      body: JSON.stringify({
        content,
        mediaList,
        visibility: "PUBLIC",
        commentsEnabled: true,
        likesEnabled: true,
      }),
    })
  );
