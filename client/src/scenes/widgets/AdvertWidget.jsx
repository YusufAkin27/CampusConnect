import { Typography, useTheme } from "@mui/material";
import FlexBetween from "components/FlexBetween";
import WidgetWrapper from "components/WidgetWrapper";

const AdvertWidget = () => {
  const { palette } = useTheme();
  const dark = palette.neutral.dark;
  const medium = palette.neutral.medium;

  return (
    <WidgetWrapper>
      <FlexBetween>
        <Typography color={dark} variant="h5" fontWeight="500">
          Campus
        </Typography>
        <Typography color={medium}>Announcements</Typography>
      </FlexBetween>
      <Typography color={medium} m="0.5rem 0">
        Backendde duyuru endpointi bulunmadığı için gösterilecek duyuru yok.
      </Typography>
    </WidgetWrapper>
  );
};

export default AdvertWidget;
