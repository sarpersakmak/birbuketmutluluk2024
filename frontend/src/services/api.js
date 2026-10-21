import axios from "axios";

import { API_BASE_URL, TOKEN_KEY } from "../utils/constants";

/**
 * Merkezi Axios instance (bölüm 38).
 * - request interceptor: JWT'yi Authorization başlığına ekler
 * - response interceptor: 401'de token'ı temizler ve login'e yönlendirir
 */
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem(TOKEN_KEY);
      // Zaten login sayfasındaysak (ör. hatalı giriş) yönlendirme yapma;
      // hatayı çağıran bileşen yakalayıp toast gösterebilsin.
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
