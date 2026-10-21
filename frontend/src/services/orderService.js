import api from "./api";

export const createOrder = (data) => api.post("/orders", data);
export const fetchMyOrders = () => api.get("/orders/my-orders");
export const fetchOrder = (id) => api.get(`/orders/${id}`);
