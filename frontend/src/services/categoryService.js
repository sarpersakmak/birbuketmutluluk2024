import api from "./api";

// Public
export const fetchCategories = () => api.get("/categories");

// Admin
export const fetchAllCategories = () => api.get("/admin/categories");
export const createCategory = (data) => api.post("/admin/categories", data);
export const updateCategory = (id, data) => api.put(`/admin/categories/${id}`, data);
export const deleteCategory = (id) => api.delete(`/admin/categories/${id}`);
