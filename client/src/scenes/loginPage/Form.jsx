import { useEffect, useState, useCallback } from "react";
import {
  Box,
  Button,
  Checkbox,
  FormControlLabel,
  TextField,
  useMediaQuery,
  Typography,
  useTheme,
  Alert,
  CircularProgress,
  Collapse,
  IconButton,
  Paper,
  Divider,
  Chip,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import GavelIcon from "@mui/icons-material/Gavel";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import { Formik } from "formik";
import * as yup from "yup";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { setLogin } from "state";
import * as authService from "services/authService";
import {
  getRequiredContractsFull,
  validateRequiredContracts,
  acceptContracts,
} from "services/contractService";

/* ────── Yup Schemas ────── */
const registerSchema = yup.object().shape({
  username: yup.string().min(3, "min 3 karakter").required("zorunlu"),
  email: yup.string().email("geçersiz e-posta").required("zorunlu"),
  password: yup.string().min(6, "min 6 karakter").required("zorunlu"),
});

const loginSchema = yup.object().shape({
  usernameOrEmail: yup.string().required("zorunlu"),
  password: yup.string().required("zorunlu"),
});

const initialValuesRegister = {
  username: "",
  email: "",
  password: "",
};

const initialValuesLogin = {
  usernameOrEmail: "",
  password: "",
};

/* ────── Sözleşme türü → okunabilir Türkçe etiket ────── */
const CONTRACT_TYPE_LABELS = {
  TERMS_OF_SERVICE: "Kullanım Koşulları",
  PRIVACY_POLICY: "Gizlilik Politikası",
  KVKK: "KVKK Aydınlatma Metni",
  EXPLICIT_CONSENT: "Açık Rıza Metni",
  COOKIE_POLICY: "Çerez Politikası",
  COMMUNICATION_PERMISSION: "İletişim İzni",
};

/* ════════════════════════════════════════════════════════
   ContractItem — tek bir sözleşme kartı
   ════════════════════════════════════════════════════════ */
const ContractItem = ({ contract, accepted, onToggle, palette }) => {
  const [expanded, setExpanded] = useState(false);
  const label =
    CONTRACT_TYPE_LABELS[contract.contractType] || contract.title || contract.contractType;

  return (
    <Paper
      elevation={0}
      sx={{
        border: `1px solid ${accepted ? palette.primary.main : palette.neutral.light}`,
        borderRadius: "12px",
        overflow: "hidden",
        transition: "border-color 0.3s ease, box-shadow 0.3s ease",
        ...(accepted && {
          boxShadow: `0 0 0 1px ${palette.primary.main}20`,
        }),
      }}
    >
      {/* Header */}
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          px: 2,
          py: 1.5,
          cursor: "pointer",
          "&:hover": { backgroundColor: palette.neutral.light + "30" },
          transition: "background-color 0.2s ease",
        }}
        onClick={() => setExpanded((prev) => !prev)}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, flex: 1, minWidth: 0 }}>
          <GavelIcon sx={{ color: palette.primary.main, fontSize: 20 }} />
          <Typography
            variant="subtitle2"
            fontWeight="600"
            sx={{
              overflow: "hidden",
              textOverflow: "ellipsis",
              whiteSpace: "nowrap",
            }}
          >
            {label}
          </Typography>
          {contract.isRequired && (
            <Chip
              label="Zorunlu"
              size="small"
              sx={{
                height: 22,
                fontSize: "0.7rem",
                fontWeight: 600,
                backgroundColor: palette.primary.main + "18",
                color: palette.primary.main,
              }}
            />
          )}
          {contract.version && (
            <Typography variant="caption" color="text.secondary">
              v{contract.version}
            </Typography>
          )}
        </Box>

        <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
          {accepted && (
            <CheckCircleOutlineIcon sx={{ color: palette.primary.main, fontSize: 20 }} />
          )}
          <IconButton size="small">
            {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
          </IconButton>
        </Box>
      </Box>

      {/* Sözleşme İçeriği */}
      <Collapse in={expanded}>
        <Divider />
        <Box
          sx={{
            px: 2,
            py: 2,
            maxHeight: 300,
            overflowY: "auto",
            backgroundColor: palette.background.default,
            "&::-webkit-scrollbar": { width: 6 },
            "&::-webkit-scrollbar-thumb": {
              backgroundColor: palette.neutral.medium,
              borderRadius: 3,
            },
          }}
        >
          <Typography
            variant="body2"
            sx={{
              whiteSpace: "pre-line",
              lineHeight: 1.7,
              color: palette.neutral.main,
              fontSize: "0.82rem",
            }}
          >
            {contract.content || "Sözleşme içeriği yükleniyor..."}
          </Typography>
        </Box>
        <Divider />
        <Box sx={{ px: 2, py: 1 }}>
          <FormControlLabel
            control={
              <Checkbox
                checked={accepted}
                onChange={() => onToggle(contract.id)}
                sx={{
                  color: palette.primary.main,
                  "&.Mui-checked": { color: palette.primary.main },
                }}
              />
            }
            label={
              <Typography variant="body2" fontWeight="500">
                {contract.isRequired
                  ? "Okudum ve kabul ediyorum"
                  : "Okudum ve onaylıyorum (isteğe bağlı)"}
              </Typography>
            }
          />
        </Box>
      </Collapse>
    </Paper>
  );
};

/* ════════════════════════════════════════════════════════
   Form — Ana login/register formu
   ════════════════════════════════════════════════════════ */
const Form = () => {
  const [pageType, setPageType] = useState("login");
  const [requiredContracts, setRequiredContracts] = useState([]);
  const [acceptedContractIds, setAcceptedContractIds] = useState(new Set());
  const [contractsLoading, setContractsLoading] = useState(false);
  const [contractsError, setContractsError] = useState("");
  const [submitError, setSubmitError] = useState("");
  const [submitSuccess, setSubmitSuccess] = useState("");
  const { palette } = useTheme();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const isNonMobile = useMediaQuery("(min-width:600px)");
  const isLogin = pageType === "login";
  const isRegister = pageType === "register";

  /* ── Sözleşmeleri yükle ── */
  useEffect(() => {
    const loadContracts = async () => {
      if (!isRegister) return;

      setContractsLoading(true);
      try {
        const contracts = await getRequiredContractsFull();
        const list = Array.isArray(contracts) ? contracts : [];
        setRequiredContracts(list);
        setAcceptedContractIds(new Set());
        setContractsError("");
      } catch {
        setRequiredContracts([]);
        setContractsError("Sözleşme servisi hazır değil. Lütfen daha sonra tekrar deneyin.");
      } finally {
        setContractsLoading(false);
      }
    };

    loadContracts();
  }, [isRegister]);

  /* ── Tek bir sözleşme checkbox toggle ── */
  const handleContractToggle = useCallback((contractId) => {
    setAcceptedContractIds((prev) => {
      const next = new Set(prev);
      if (next.has(contractId)) {
        next.delete(contractId);
      } else {
        next.add(contractId);
      }
      return next;
    });
  }, []);

  /* ── Tüm zorunlu sözleşmeler kabul edildi mi? ── */
  const allRequiredAccepted = requiredContracts
    .filter((c) => c.isRequired || c.required)
    .every((c) => acceptedContractIds.has(c.id));

  /* ── Kayıt işlemi ── */
  const register = async (values, onSubmitProps) => {
    setSubmitError("");
    setSubmitSuccess("");

    // Zorunlu sözleşme kontrolü
    const mandatoryContracts = requiredContracts.filter((c) => c.isRequired || c.required);
    if (mandatoryContracts.length > 0 && !allRequiredAccepted) {
      setSubmitError("Devam etmek için tüm zorunlu sözleşmeleri okumalı ve kabul etmelisiniz.");
      return;
    }

    try {
      // 1) Backend'de zorunlu sözleşme validasyonu
      const idsToValidate = [...acceptedContractIds];
      if (idsToValidate.length > 0) {
        const validation = await validateRequiredContracts(idsToValidate);
        if (validation && validation.valid === false) {
          setSubmitError(
            validation.message || "Eksik zorunlu sözleşmeler bulunmaktadır."
          );
          return;
        }
      }

      // 2) Kullanıcı kaydı
      const registerResponse = await authService.register({
        username: values.username,
        email: values.email,
        password: values.password,
      });

      // 3) Sözleşme kabulünü kaydet
      // register response'dan userId almayı dene
      const userId =
        registerResponse?.data?.id ||
        registerResponse?.data?.userId ||
        registerResponse?.id ||
        registerResponse?.userId;

      if (userId && acceptedContractIds.size > 0) {
        try {
          await acceptContracts({
            userId,
            acceptedContractIds: [...acceptedContractIds],
          });
        } catch (contractErr) {
          // Sözleşme kabulü başarısız olsa bile kayıt tamamlandı
          console.warn("Sözleşme kabul kaydı başarısız:", contractErr);
        }
      }

      onSubmitProps.resetForm();
      setAcceptedContractIds(new Set());
      setSubmitSuccess("Kayıt başarıyla oluşturuldu! Giriş yapabilirsiniz.");
      setPageType("login");
    } catch (error) {
      setSubmitError(error.message);
    }
  };

  /* ── Giriş işlemi ── */
  const login = async (values, onSubmitProps) => {
    setSubmitError("");
    setSubmitSuccess("");

    try {
      const loggedIn = await authService.login({
        usernameOrEmail: values.usernameOrEmail,
        password: values.password,
      });
      onSubmitProps.resetForm();
      dispatch(
        setLogin({
          user: loggedIn.user,
          accessToken: loggedIn.accessToken,
          refreshToken: loggedIn.refreshToken,
        })
      );
      navigate("/home");
    } catch (error) {
      setSubmitError(error.message);
    }
  };

  const handleFormSubmit = async (values, onSubmitProps) => {
    if (isLogin) await login(values, onSubmitProps);
    if (isRegister) await register(values, onSubmitProps);
  };

  /* ── Tümünü kabul et / kaldır ── */
  const handleAcceptAll = () => {
    if (allRequiredAccepted && acceptedContractIds.size === requiredContracts.length) {
      setAcceptedContractIds(new Set());
    } else {
      setAcceptedContractIds(new Set(requiredContracts.map((c) => c.id)));
    }
  };

  return (
    <Formik
      onSubmit={handleFormSubmit}
      initialValues={isLogin ? initialValuesLogin : initialValuesRegister}
      validationSchema={isLogin ? loginSchema : registerSchema}
    >
      {({
        values,
        errors,
        touched,
        handleBlur,
        handleChange,
        handleSubmit,
        isSubmitting,
        resetForm,
      }) => (
        <form onSubmit={handleSubmit}>
          {submitError && (
            <Alert severity="error" sx={{ mb: "1rem", borderRadius: "10px" }}>
              {submitError}
            </Alert>
          )}
          {submitSuccess && (
            <Alert severity="success" sx={{ mb: "1rem", borderRadius: "10px" }}>
              {submitSuccess}
            </Alert>
          )}
          <Box
            display="grid"
            gap="30px"
            gridTemplateColumns="repeat(4, minmax(0, 1fr))"
            sx={{
              "& > div": { gridColumn: isNonMobile ? undefined : "span 4" },
            }}
          >
            {isRegister && (
              <>
                <TextField
                  label="Kullanıcı Adı"
                  onBlur={handleBlur}
                  onChange={handleChange}
                  value={values.username}
                  name="username"
                  error={Boolean(touched.username) && Boolean(errors.username)}
                  helperText={touched.username && errors.username}
                  sx={{ gridColumn: "span 4" }}
                />
              </>
            )}

            {isLogin && (
              <TextField
                label="Kullanıcı Adı veya E-posta"
                onBlur={handleBlur}
                onChange={handleChange}
                value={values.usernameOrEmail}
                name="usernameOrEmail"
                error={
                  Boolean(touched.usernameOrEmail) &&
                  Boolean(errors.usernameOrEmail)
                }
                helperText={touched.usernameOrEmail && errors.usernameOrEmail}
                sx={{ gridColumn: "span 4" }}
              />
            )}
            {isRegister && (
              <TextField
                label="E-posta"
                onBlur={handleBlur}
                onChange={handleChange}
                value={values.email}
                name="email"
                error={Boolean(touched.email) && Boolean(errors.email)}
                helperText={touched.email && errors.email}
                sx={{ gridColumn: "span 4" }}
              />
            )}
            <TextField
              label="Şifre"
              type="password"
              onBlur={handleBlur}
              onChange={handleChange}
              value={values.password}
              name="password"
              error={Boolean(touched.password) && Boolean(errors.password)}
              helperText={touched.password && errors.password}
              sx={{ gridColumn: "span 4" }}
            />

            {/* ── Sözleşme Bölümü ── */}
            {isRegister && (
              <Box gridColumn="span 4">
                {contractsLoading ? (
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      gap: 1.5,
                      py: 3,
                    }}
                  >
                    <CircularProgress size={20} />
                    <Typography color="text.secondary" variant="body2">
                      Sözleşmeler yükleniyor...
                    </Typography>
                  </Box>
                ) : contractsError ? (
                  <Alert severity="warning" sx={{ borderRadius: "10px" }}>
                    {contractsError}
                  </Alert>
                ) : requiredContracts.length > 0 ? (
                  <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
                    {/* Başlık ve Tümünü kabul et */}
                    <Box
                      sx={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        mb: 0.5,
                      }}
                    >
                      <Typography
                        variant="subtitle1"
                        fontWeight="700"
                        sx={{ display: "flex", alignItems: "center", gap: 1 }}
                      >
                        <GavelIcon sx={{ fontSize: 22, color: palette.primary.main }} />
                        Sözleşmeler
                      </Typography>
                      <Button
                        size="small"
                        variant="text"
                        onClick={handleAcceptAll}
                        sx={{
                          textTransform: "none",
                          fontWeight: 600,
                          fontSize: "0.8rem",
                        }}
                      >
                        {allRequiredAccepted && acceptedContractIds.size === requiredContracts.length
                          ? "Tümünü Kaldır"
                          : "Tümünü Kabul Et"}
                      </Button>
                    </Box>

                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{ mb: 0.5, lineHeight: 1.5 }}
                    >
                      Kayıt olmak için lütfen aşağıdaki zorunlu sözleşmeleri okuyup kabul edin.
                      Her bir sözleşmeye tıklayarak içeriğini okuyabilirsiniz.
                    </Typography>

                    {/* Sözleşme Kartları */}
                    {requiredContracts.map((contract) => (
                      <ContractItem
                        key={contract.id}
                        contract={contract}
                        accepted={acceptedContractIds.has(contract.id)}
                        onToggle={handleContractToggle}
                        palette={palette}
                      />
                    ))}

                    {/* Kabul durumu özeti */}
                    <Typography
                      variant="caption"
                      sx={{
                        color: allRequiredAccepted
                          ? palette.primary.main
                          : palette.neutral.medium,
                        fontWeight: 600,
                        mt: 0.5,
                      }}
                    >
                      {acceptedContractIds.size} / {requiredContracts.length} sözleşme kabul edildi
                      {!allRequiredAccepted && " — Zorunlu sözleşmeleri kabul etmelisiniz"}
                    </Typography>
                  </Box>
                ) : (
                  <Typography color={palette.neutral.medium} variant="body2">
                    Zorunlu sözleşme bulunamadı.
                  </Typography>
                )}
              </Box>
            )}
          </Box>

          {/* BUTTONS */}
          <Box>
            <Button
              fullWidth
              type="submit"
              disabled={isSubmitting || (isRegister && requiredContracts.length > 0 && !allRequiredAccepted)}
              sx={{
                m: "2rem 0",
                p: "1rem",
                backgroundColor: palette.primary.main,
                color: palette.background.alt,
                borderRadius: "10px",
                fontWeight: 600,
                textTransform: "none",
                fontSize: "1rem",
                "&:hover": { color: palette.primary.main },
                "&:disabled": {
                  backgroundColor: palette.neutral.light,
                  color: palette.neutral.medium,
                },
              }}
            >
              {isSubmitting ? (
                <CircularProgress size={24} />
              ) : isLogin ? (
                "Giriş Yap"
              ) : (
                "Kayıt Ol"
              )}
            </Button>
            <Typography
              onClick={() => {
                setPageType(isLogin ? "register" : "login");
                setSubmitError("");
                setSubmitSuccess("");
                setAcceptedContractIds(new Set());
                resetForm();
              }}
              sx={{
                textDecoration: "underline",
                color: palette.primary.main,
                "&:hover": {
                  cursor: "pointer",
                  color: palette.primary.light,
                },
              }}
            >
              {isLogin
                ? "Hesabınız yok mu? Kayıt olun."
                : "Zaten hesabınız var mı? Giriş yapın."}
            </Typography>
          </Box>
        </form>
      )}
    </Formik>
  );
};

export default Form;
