import { Box } from "@mui/material";
import { Person } from "@mui/icons-material";

const UserImage = ({ image, size = "60px" }) => {
  const imageUrl = image?.startsWith("http") ? image : image || "";

  return (
    <Box
      width={size}
      height={size}
      borderRadius="50%"
      overflow="hidden"
      display="flex"
      alignItems="center"
      justifyContent="center"
      backgroundColor="neutral.light"
    >
      {imageUrl ? (
        <img
          style={{ objectFit: "cover", borderRadius: "50%" }}
          width={size}
          height={size}
          alt="user"
          src={imageUrl}
        />
      ) : (
        <Person />
      )}
    </Box>
  );
};

export default UserImage;
