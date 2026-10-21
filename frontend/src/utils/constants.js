// Uygulama geneli sabitler

// Üretimde Vite ortam değişkeni ile verilir: VITE_API_BASE_URL
// (ör. https://api.birbuketmutluluk.com/api). Dev'de varsayılan localhost.
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

// JWT'nin localStorage'da tutulduğu anahtar
export const TOKEN_KEY = "bbm_token";

export const ROLES = {
  USER: "USER",
  ADMIN: "ADMIN",
};
