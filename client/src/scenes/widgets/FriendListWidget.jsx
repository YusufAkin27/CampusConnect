import { Box, Typography, useTheme } from "@mui/material";
import Friend from "components/Friend";
import WidgetWrapper from "components/WidgetWrapper";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setFriends } from "state";
import { getMyFriends, getUserFriends } from "services/friendService";

const FriendListWidget = ({ userId }) => {
  const dispatch = useDispatch();
  const { palette } = useTheme();
  const currentUser = useSelector((state) => state.user);
  const friends = useSelector((state) => state.friends);

  const getFriends = async () => {
    try {
      const currentAuthUserId = currentUser?.authUserId || currentUser?.id;
      const data =
        String(userId) === String(currentAuthUserId)
          ? await getMyFriends()
          : await getUserFriends(userId);
      dispatch(setFriends({ friends: data }));
    } catch {
      dispatch(setFriends({ friends: [] }));
    }
  };

  useEffect(() => {
    getFriends();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <WidgetWrapper>
      <Typography
        color={palette.neutral.dark}
        variant="h5"
        fontWeight="500"
        sx={{ mb: "1.5rem" }}
      >
        Friend List
      </Typography>
      <Box display="flex" flexDirection="column" gap="1.5rem">
        {friends.length > 0 ? (
          friends.map((friendship) => {
            const friend = friendship.friend || friendship;
            const name =
              friend.displayName ||
              [friend.firstName, friend.lastName].filter(Boolean).join(" ") ||
              friend.username ||
              "CampusConnect User";
            return (
              <Friend
                key={friend.authUserId || friend.id || friendship.id}
                friendId={friend.authUserId || friend.id}
                name={name}
                subtitle={friend.username ? `@${friend.username}` : ""}
                userPicturePath={friend.profileImageUrl}
              />
            );
          })
        ) : (
          <Typography color={palette.neutral.medium}>Henüz arkadaş yok.</Typography>
        )}
      </Box>
    </WidgetWrapper>
  );
};

export default FriendListWidget;
