import api from "./api";

export const getProvinces = () => api.get("/provinces");
export const getDistricts = (provinceId) => api.get(`/provinces/${provinceId}/districts`);
