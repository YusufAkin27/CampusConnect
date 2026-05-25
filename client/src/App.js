import { BrowserRouter, Navigate, Routes, Route } from "react-router-dom";
import HomePage from "scenes/homePage";
import LoginPage from "scenes/loginPage";
import ProfilePage from "scenes/profilePage";
import SettingsPage from "scenes/settingsPage";
import { useEffect, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { createTheme } from "@mui/material/styles";
import { themeSettings } from "./theme";
import { setLogout, setUser } from "state";
import { getCurrentAuthUser } from "services/authService";
import { getMyProfile } from "services/userService";
import { clearTokens, setTokens } from "utils/tokenStorage";

function App() {
  const dispatch = useDispatch();
  const mode = useSelector((state) => state.mode);
  const token = useSelector((state) => state.token);
  const refreshToken = useSelector((state) => state.refreshToken);
  const theme = useMemo(() => createTheme(themeSettings(mode)), [mode]);
  const isAuth = Boolean(token);

  useEffect(() => {
    const loadSession = async () => {
      if (!token) return;

      try {
        const authUser = await getCurrentAuthUser();
        setTokens({ accessToken: token, refreshToken, user: authUser });

        try {
          const profile = await getMyProfile();
          dispatch(setUser({ user: { ...authUser, ...profile } }));
        } catch {
          dispatch(setUser({ user: authUser }));
        }
      } catch {
        clearTokens();
        dispatch(setLogout());
      }
    };

    loadSession();
  }, [dispatch, refreshToken, token]);

  return (
    <div className="app">
      <BrowserRouter>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <Routes>
            <Route
              path="/"
              element={isAuth ? <Navigate to="/home" replace /> : <LoginPage />}
            />
            <Route
              path="/home"
              element={isAuth ? <HomePage /> : <Navigate to="/" replace />}
            />
            <Route
              path="/profile/:userId"
              element={isAuth ? <ProfilePage /> : <Navigate to="/" replace />}
            />
            <Route
              path="/settings"
              element={isAuth ? <SettingsPage /> : <Navigate to="/" replace />}
            />
          </Routes>
        </ThemeProvider>
      </BrowserRouter>
    </div>
  );
}

export default App;
