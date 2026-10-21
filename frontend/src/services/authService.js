import api from "./api";

// Backend API sözleşmesi ile birebir uyumlu uç çağrıları (Kural 7-8).

export const registerRequest = (data) => api.post("/auth/register", data);

export const loginRequest = (data) => api.post("/auth/login", data);

export const getMe = () => api.get("/auth/me");
