import api from "./api";

export const updateProfile = (data) => api.put("/users/me", data);
export const changePassword = (data) => api.put("/users/me/password", data);
