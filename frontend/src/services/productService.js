import api from "./api";

// Public
export const fetchProducts = (params) => api.get("/products", { params });
export const fetchFeatured = () => api.get("/products/featured");
export const fetchProductBySlug = (slug) => api.get(`/products/${slug}`);

// Admin
export const createProduct = (data) => api.post("/admin/products", data);
export const updateProduct = (id, data) => api.put(`/admin/products/${id}`, data);
export const deleteProduct = (id) => api.delete(`/admin/products/${id}`);
