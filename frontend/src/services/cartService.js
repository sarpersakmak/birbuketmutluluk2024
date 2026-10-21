import api from "./api";

export const getCart = () => api.get("/cart");
export const addItem = (data) => api.post("/cart/items", data);
export const updateItem = (itemId, data) => api.put(`/cart/items/${itemId}`, data);
export const removeItem = (itemId) => api.delete(`/cart/items/${itemId}`);
export const clearCart = () => api.delete("/cart");
