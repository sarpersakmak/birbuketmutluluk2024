import api from "./api";

// Dashboard
export const fetchDashboard = () => api.get("/admin/dashboard");

// Kullanıcılar
export const fetchUsers = (q) => api.get("/admin/users", { params: q ? { q } : {} });
export const fetchUser = (id) => api.get(`/admin/users/${id}`);
export const updateUserStatus = (id, active) => api.put(`/admin/users/${id}/status`, { active });

// Siparişler
export const fetchAdminOrders = (status) =>
  api.get("/admin/orders", { params: status ? { status } : {} });
export const fetchAdminOrder = (id) => api.get(`/admin/orders/${id}`);
export const updateOrderStatus = (id, status) =>
  api.put(`/admin/orders/${id}/status`, { status });
