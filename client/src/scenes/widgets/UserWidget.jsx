import {
  ManageAccountsOutlined,
  BadgeOutlined,
  InfoOutlined,
  PhoneOutlined,
} from "@mui/icons-material";
import { Box, Typography, Divider, useTheme } from "@mui/material";
import UserImage from "components/UserImage";
import FlexBetween from "components/FlexBetween";
import WidgetWrapper from "components/WidgetWrapper";
import { useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getProfileByAuthUserId } from "services/userService";
import { getUserFriends } from "services/friendService";

const UserWidget = ({ userId, profileImageUrl }) => {
  const [user, setUser] = useState(null);
  const [friendCount, setFriendCount] = useState(0);
  const { palette } = useTheme();
  const navigate = useNavigate();
  const currentUser = useSelector((state) => state.user);
  const dark = palette.neutral.dark;
  const medium = palette.neutral.medium;
  const main = palette.neutral.main;

  const getUser = async () => {
    if (!userId) {
      setUser(currentUser);
      return;
    }

    try {
      const data = await getProfileByAuthUserId(userId);
      setUser(data);
    } catch {
      setUser(currentUser);
    }

    try {
      const friends = await getUserFriends(userId);
      setFriendCount(friends.length);
    } catch {
      setFriendCount(0);
    }
  };

  useEffect(() => {
    getUser();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  if (!user) {
    return null;
  }

  const {
    authUserId,
    username,
    firstName,
    lastName,
    bio,
    phoneNumber,
    profileVisibility,
  } = user;
  const displayName =
    [firstName, lastName].filter(Boolean).join(" ") || username || "CampusConnect User";
  const targetId = authUserId || userId || user.id;

  return (
    <WidgetWrapper>
      {/* FIRST ROW */}
      <FlexBetween
        gap="0.5rem"
        pb="1.1rem"
        onClick={() => targetId && navigate(`/profile/${targetId}`)}
      >
        <FlexBetween gap="1rem">
          <UserImage image={profileImageUrl || user.profileImageUrl} />
          <Box>
            <Typography
              variant="h4"
              color={dark}
              fontWeight="500"
              sx={{
                "&:hover": {
                  color: palette.primary.light,
                  cursor: "pointer",
                },
              }}
            >
              {displayName}
            </Typography>
            {username && <Typography color={medium}>@{username}</Typography>}
          </Box>
        </FlexBetween>
        <ManageAccountsOutlined />
      </FlexBetween>

      <Divider />

      {/* SECOND ROW */}
      <Box p="1rem 0">
        {bio && (
          <Box display="flex" alignItems="center" gap="1rem" mb="0.5rem">
            <InfoOutlined fontSize="large" sx={{ color: main }} />
            <Typography color={medium}>{bio}</Typography>
          </Box>
        )}
        {phoneNumber && (
          <Box display="flex" alignItems="center" gap="1rem">
            <PhoneOutlined fontSize="large" sx={{ color: main }} />
            <Typography color={medium}>{phoneNumber}</Typography>
          </Box>
        )}
        {!bio && !phoneNumber && (
          <Box display="flex" alignItems="center" gap="1rem">
            <BadgeOutlined fontSize="large" sx={{ color: main }} />
            <Typography color={medium}>Profil bilgileri henüz tamamlanmadı.</Typography>
          </Box>
        )}
      </Box>

      <Divider />

      {/* THIRD ROW */}
      <Box p="1rem 0">
        <FlexBetween mb="0.5rem">
          <Typography color={medium}>Friends</Typography>
          <Typography color={main} fontWeight="500">
            {friendCount}
          </Typography>
        </FlexBetween>
        <FlexBetween>
          <Typography color={medium}>Profile visibility</Typography>
          <Typography color={main} fontWeight="500">
            {profileVisibility || "PUBLIC"}
          </Typography>
        </FlexBetween>
      </Box>
    </WidgetWrapper>
  );
};

export default UserWidget;
