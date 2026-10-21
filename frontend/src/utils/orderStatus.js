export const ORDER_STATUS_LABELS = {
  PENDING: "Beklemede",
  CONFIRMED: "Onaylandı",
  PREPARING: "Hazırlanıyor",
  READY: "Hazır",
  SHIPPED: "Kargoda",
  DELIVERED: "Teslim Edildi",
  CANCELLED: "İptal Edildi",
};

export const statusLabel = (status) => ORDER_STATUS_LABELS[status] || status;

// Admin durum geçiş haritası (backend ile birebir aynı - bölüm 17).
export const ALLOWED_TRANSITIONS = {
  PENDING: ["CONFIRMED", "CANCELLED"],
  CONFIRMED: ["PREPARING", "CANCELLED"],
  PREPARING: ["READY", "CANCELLED"],
  READY: ["SHIPPED", "CANCELLED"],
  SHIPPED: ["DELIVERED"],
  DELIVERED: [],
  CANCELLED: [],
};

export const allowedNext = (status) => ALLOWED_TRANSITIONS[status] || [];

