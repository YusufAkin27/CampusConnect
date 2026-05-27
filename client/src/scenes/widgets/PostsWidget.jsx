import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setPosts } from "state";
import PostWidget from "./PostWidget";
import { getFeed, getUserPosts } from "services/postService";
import { Typography } from "@mui/material";

const PostsWidget = ({ userId, isProfile = false }) => {
  const dispatch = useDispatch();
  const posts = useSelector((state) => state.posts);

  const getPosts = async () => {
    try {
      const data = isProfile ? await getUserPosts(userId) : await getFeed();
      dispatch(setPosts({ posts: data }));
    } catch {
      dispatch(setPosts({ posts: [] }));
    }
  };

  useEffect(() => {
    getPosts();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <>
      {posts.length > 0 ? (
        posts.map(
        ({
          id,
          authUserId,
          author,
          content,
          mediaList,
          likeCount,
          commentCount,
          likedByMe,
        }) => (
          <PostWidget
            key={id}
            postUserId={authUserId}
            name={author?.displayName || author?.username || "CampusConnect User"}
            description={content}
            subtitle={author?.username ? `@${author.username}` : ""}
            mediaList={mediaList}
            userPicturePath={author?.profileImageUrl}
            likeCount={likeCount}
            commentCount={commentCount}
            likedByMe={likedByMe}
          />
        )
        )
      ) : (
        <Typography mt="2rem" textAlign="center">
          Henüz gönderi yok.
        </Typography>
      )}
    </>
  );
};

export default PostsWidget;
