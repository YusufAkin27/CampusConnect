import {
  ChatBubbleOutlineOutlined,
  FavoriteBorderOutlined,
  FavoriteOutlined,
  ShareOutlined,
} from "@mui/icons-material";
import { Box, Divider, IconButton, Typography, useTheme } from "@mui/material";
import FlexBetween from "components/FlexBetween";
import Friend from "components/Friend";
import WidgetWrapper from "components/WidgetWrapper";
import { useState } from "react";

const PostWidget = ({
  postUserId,
  name,
  description,
  subtitle,
  mediaList = [],
  userPicturePath,
  likeCount = 0,
  commentCount = 0,
  likedByMe = false,
}) => {
  const [isComments, setIsComments] = useState(false);
  const firstMedia = mediaList?.[0];

  const { palette } = useTheme();
  const main = palette.neutral.main;
  const primary = palette.primary.main;

  return (
    <WidgetWrapper m="2rem 0">
      <Friend
        friendId={postUserId}
        name={name}
        subtitle={subtitle}
        userPicturePath={userPicturePath}
      />
      <Typography color={main} sx={{ mt: "1rem" }}>
        {description}
      </Typography>
      {firstMedia?.mediaUrl && (
        <img
          width="100%"
          height="auto"
          alt="post"
          style={{ borderRadius: "0.75rem", marginTop: "0.75rem" }}
          src={firstMedia.mediaUrl}
        />
      )}
      <FlexBetween mt="0.25rem">
        <FlexBetween gap="1rem">
          <FlexBetween gap="0.3rem">
            <IconButton disabled>
              {likedByMe ? (
                <FavoriteOutlined sx={{ color: primary }} />
              ) : (
                <FavoriteBorderOutlined />
              )}
            </IconButton>
            <Typography>{likeCount}</Typography>
          </FlexBetween>

          <FlexBetween gap="0.3rem">
            <IconButton onClick={() => setIsComments(!isComments)}>
              <ChatBubbleOutlineOutlined />
            </IconButton>
            <Typography>{commentCount}</Typography>
          </FlexBetween>
        </FlexBetween>

        <IconButton>
          <ShareOutlined />
        </IconButton>
      </FlexBetween>
      {isComments && (
        <Box mt="0.5rem">
          <Divider />
          <Typography sx={{ color: main, m: "0.5rem 0", pl: "1rem" }}>
            Yorum servisi için public controller bulunamadı.
          </Typography>
          <Divider />
        </Box>
      )}
    </WidgetWrapper>
  );
};

export default PostWidget;
