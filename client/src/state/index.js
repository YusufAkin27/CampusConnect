import { createSlice } from "@reduxjs/toolkit";
import { getAccessToken, getRefreshToken, getStoredAuthUser } from "utils/tokenStorage";

const initialState = {
  mode: "light",
  user: getStoredAuthUser(),
  token: getAccessToken(),
  refreshToken: getRefreshToken(),
  posts: [],
  friends: [],
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setMode: (state) => {
      state.mode = state.mode === "light" ? "dark" : "light";
    },
    setLogin: (state, action) => {
      state.user = action.payload.user;
      state.token = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
    },
    setUser: (state, action) => {
      state.user = action.payload.user;
    },
    setLogout: (state) => {
      state.user = null;
      state.token = null;
      state.refreshToken = null;
      state.posts = [];
      state.friends = [];
    },
    setFriends: (state, action) => {
      state.friends = action.payload.friends;
    },
    setPosts: (state, action) => {
      state.posts = action.payload.posts;
    },
    setPost: (state, action) => {
      const updatedPosts = state.posts.map((post) =>
        post.id === action.payload.post.id ? action.payload.post : post
      );
      state.posts = updatedPosts;
    },
  },
});

export const { setMode, setLogin, setUser, setLogout, setFriends, setPosts, setPost } =
  authSlice.actions;
export default authSlice.reducer;
