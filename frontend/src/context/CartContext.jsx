import { createContext, useCallback, useContext, useEffect, useState } from "react";

import * as cartService from "../services/cartService";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);

  const refresh = useCallback(async () => {
    if (!isAuthenticated) {
      setCart(null);
      return;
    }
    setLoading(true);
    try {
      const res = await cartService.getCart();
      setCart(res.data);
    } catch {
      // sessizce yoksay; 401 interceptor zaten yönlendirir
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  // Giriş/çıkış durumuna göre sepeti yükle/temizle.
  useEffect(() => {
    refresh();
  }, [refresh]);

  const addItem = async (productId, quantity = 1) => {
    const res = await cartService.addItem({ productId, quantity });
    setCart(res.data);
    return res.data;
  };

  const updateItem = async (itemId, quantity) => {
    const res = await cartService.updateItem(itemId, { quantity });
    setCart(res.data);
    return res.data;
  };

  const removeItem = async (itemId) => {
    const res = await cartService.removeItem(itemId);
    setCart(res.data);
    return res.data;
  };

  const clear = async () => {
    const res = await cartService.clearCart();
    setCart(res.data);
    return res.data;
  };

  const value = {
    cart,
    loading,
    cartCount: cart?.totalItems || 0,
    addItem,
    updateItem,
    removeItem,
    clear,
    refresh,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) {
    throw new Error("useCart, CartProvider içinde kullanılmalıdır.");
  }
  return ctx;
}
