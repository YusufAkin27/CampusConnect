import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Checkbox,
  CircularProgress,
  FormControlLabel,
  TextField,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import Navbar from "scenes/navbar";
import WidgetWrapper from "components/WidgetWrapper";
import { changePassword } from "services/authService";
import {
  getNotificationPreferences,
  updateNotificationPreferences,
} from "services/notificationService";
import { getActiveContracts } from "services/contractService";

const SettingsPage = () => {
  const { palette } = useTheme();
  const isNonMobileScreens = useMediaQuery("(min-width:1000px)");
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [preferences, setPreferences] = useState(null);
  const [contracts, setContracts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const loadSettings = async () => {
      try {
        const [preferenceData, contractData] = await Promise.all([
          getNotificationPreferences().catch(() => null),
          getActiveContracts().catch(() => []),
        ]);
        setPreferences(preferenceData);
        setContracts(Array.isArray(contractData) ? contractData : []);
      } finally {
        setLoading(false);
      }
    };

    loadSettings();
  }, []);

  const handlePasswordChange = (event) => {
    setPasswordForm((current) => ({
      ...current,
      [event.target.name]: event.target.value,
    }));
  };

  const handlePreferenceChange = (event) => {
    setPreferences((current) => ({
      ...current,
      [event.target.name]: event.target.checked,
    }));
  };

  const submitPassword = async () => {
    setError("");
    setMessage("");
    try {
      await changePassword(passwordForm);
      setPasswordForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
      setMessage("Şifre güncellendi.");
    } catch (err) {
      setError(err.message);
    }
  };

  const submitPreferences = async () => {
    if (!preferences) return;

    setError("");
    setMessage("");
    try {
      const updated = await updateNotificationPreferences({
        inAppEnabled: preferences.inAppEnabled,
        pushEnabled: preferences.pushEnabled,
        emailEnabled: preferences.emailEnabled,
        chatNotificationsEnabled: preferences.chatNotificationsEnabled,
        eventNotificationsEnabled: preferences.eventNotificationsEnabled,
        friendNotificationsEnabled: preferences.friendNotificationsEnabled,
        postNotificationsEnabled: preferences.postNotificationsEnabled,
        marketingNotificationsEnabled: preferences.marketingNotificationsEnabled,
        quietHoursEnabled: preferences.quietHoursEnabled,
        quietHoursStart: preferences.quietHoursStart,
        quietHoursEnd: preferences.quietHoursEnd,
      });
      setPreferences(updated);
      setMessage("Bildirim tercihleri güncellendi.");
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <Box>
      <Navbar />
      <Box
        width="100%"
        padding="2rem 6%"
        display={isNonMobileScreens ? "grid" : "block"}
        gridTemplateColumns="1fr 1fr"
        gap="2rem"
      >
        <WidgetWrapper>
          <Typography color={palette.neutral.dark} variant="h5" fontWeight="500" mb="1rem">
            Security
          </Typography>
          {message && <Alert severity="success" sx={{ mb: "1rem" }}>{message}</Alert>}
          {error && <Alert severity="error" sx={{ mb: "1rem" }}>{error}</Alert>}
          <Box display="grid" gap="1rem">
            <TextField
              label="Current Password"
              name="currentPassword"
              type="password"
              value={passwordForm.currentPassword}
              onChange={handlePasswordChange}
            />
            <TextField
              label="New Password"
              name="newPassword"
              type="password"
              value={passwordForm.newPassword}
              onChange={handlePasswordChange}
            />
            <TextField
              label="Confirm Password"
              name="confirmPassword"
              type="password"
              value={passwordForm.confirmPassword}
              onChange={handlePasswordChange}
            />
            <Button
              onClick={submitPassword}
              disabled={
                !passwordForm.currentPassword ||
                !passwordForm.newPassword ||
                !passwordForm.confirmPassword
              }
              sx={{
                backgroundColor: palette.primary.main,
                color: palette.background.alt,
                "&:hover": { color: palette.primary.main },
              }}
            >
              Save Password
            </Button>
          </Box>
        </WidgetWrapper>

        <WidgetWrapper sx={{ mt: isNonMobileScreens ? 0 : "2rem" }}>
          <Typography color={palette.neutral.dark} variant="h5" fontWeight="500" mb="1rem">
            Notifications
          </Typography>
          {loading ? (
            <CircularProgress size={24} />
          ) : preferences ? (
            <Box display="grid" gap="0.5rem">
              {[
                ["inAppEnabled", "In-app"],
                ["pushEnabled", "Push"],
                ["emailEnabled", "Email"],
                ["chatNotificationsEnabled", "Chat"],
                ["eventNotificationsEnabled", "Events"],
                ["friendNotificationsEnabled", "Friends"],
                ["postNotificationsEnabled", "Posts"],
                ["marketingNotificationsEnabled", "Marketing"],
                ["quietHoursEnabled", "Quiet hours"],
              ].map(([name, label]) => (
                <FormControlLabel
                  key={name}
                  control={
                    <Checkbox
                      name={name}
                      checked={Boolean(preferences[name])}
                      onChange={handlePreferenceChange}
                    />
                  }
                  label={label}
                />
              ))}
              <Button
                onClick={submitPreferences}
                sx={{
                  backgroundColor: palette.primary.main,
                  color: palette.background.alt,
                  "&:hover": { color: palette.primary.main },
                }}
              >
                Save Preferences
              </Button>
            </Box>
          ) : (
            <Typography color={palette.neutral.medium}>
              Bildirim tercihleri backendde şu an yanıt vermiyor.
            </Typography>
          )}
        </WidgetWrapper>

        <WidgetWrapper sx={{ mt: "2rem", gridColumn: "1 / -1" }}>
          <Typography color={palette.neutral.dark} variant="h5" fontWeight="500" mb="1rem">
            Contracts
          </Typography>
          {contracts.length > 0 ? (
            contracts.map((contract) => (
              <Box key={contract.id} mb="0.75rem">
                <Typography color={palette.neutral.main} fontWeight="500">
                  {contract.title}
                </Typography>
                <Typography color={palette.neutral.medium}>
                  Version {contract.version}
                </Typography>
              </Box>
            ))
          ) : (
            <Typography color={palette.neutral.medium}>
              Sözleşme servisi hazır değil veya aktif sözleşme yok.
            </Typography>
          )}
          <Typography color={palette.neutral.medium} mt="1rem">
            Kabul geçmişi endpointi UUID kullanıcı ID beklediği için mevcut auth kullanıcı ID ile çağrılmadı.
          </Typography>
        </WidgetWrapper>
      </Box>
    </Box>
  );
};

export default SettingsPage;
