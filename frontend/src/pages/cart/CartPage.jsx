import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import ConfirmModal from "../../components/ConfirmModal";
import { useCart } from "../../context/CartContext";
import { formatPrice } from "../../utils/format";

const PLACEHOLDER = "https://placehold.co/160x160?text=Gorsel+Yok";

export default function CartPage() {
  const { cart, loading, updateItem, removeItem, clear } = useCart();
  const navigate = useNavigate();
  const [busyId, setBusyId] = useState(null);
  const [confirmClear, setConfirmClear] = useState(false);
  const [clearing, setClearing] = useState(false);

  const changeQty = async (item, nextQty) => {
    if (nextQty < 1) return;
    setBusyId(item.id);
    try {
      await updateItem(item.id, nextQty);
    } catch (err) {
      toast.error(err.response?.data?.message || "Adet güncellenemedi.");
    } finally {
      setBusyId(null);
    }
  };

  const handleRemove = async (item) => {
    setBusyId(item.id);
    try {
      await removeItem(item.id);
      toast.info("Ürün sepetten çıkarıldı.");
    } catch (err) {
      toast.error(err.response?.data?.message || "Ürün çıkarılamadı.");
    } finally {
      setBusyId(null);
    }
  };

  const handleClear = async () => {
    setClearing(true);
    try {
      await clear();
      toast.info("Sepet boşaltıldı.");
      setConfirmClear(false);
    } catch {
      toast.error("Sepet boşaltılamadı.");
    } finally {
      setClearing(false);
    }
  };

  const handleCheckout = () => {
    navigate("/checkout");
  };

  if (loading && !cart) return <p className="bbm-status">Sepet yükleniyor...</p>;

  const items = cart?.items || [];

  if (items.length === 0) {
    return (
      <section className="bbm-page">
        <h1>Sepetim</h1>
        <div className="bbm-empty">
          <p>Sepetiniz şu anda boş.</p>
          <Link to="/urunler" className="bbm-btn">Ürünleri keşfet</Link>
        </div>
      </section>
    );
  }

  return (
    <section className="bbm-page">
      <h1>Sepetim</h1>

      <div className="bbm-cart">
        <div className="bbm-cart-items">
          {items.map((item) => (
            <div className="bbm-cart-item" key={item.id}>
              <Link to={`/urunler/${item.productSlug}`} className="bbm-cart-thumb">
                <img
                  src={item.mainImageUrl || PLACEHOLDER}
                  alt={item.productName}
                  onError={(e) => {
                    e.currentTarget.src = PLACEHOLDER;
                  }}
                />
              </Link>

              <div className="bbm-cart-info">
                <Link to={`/urunler/${item.productSlug}`} className="bbm-cart-name">
                  {item.productName}
                </Link>
                <span className="bbm-muted">{formatPrice(item.unitPrice)} / adet</span>
              </div>

              <div className="bbm-qty-control">
                <button
                  type="button"
                  onClick={() => changeQty(item, item.quantity - 1)}
                  disabled={busyId === item.id || item.quantity <= 1}
                >
                  −
                </button>
                <span>{item.quantity}</span>
                <button
                  type="button"
                  onClick={() => changeQty(item, item.quantity + 1)}
                  disabled={busyId === item.id || item.quantity >= item.stockQuantity}
                >
                  +
                </button>
              </div>

              <div className="bbm-cart-subtotal">{formatPrice(item.subtotal)}</div>

              <button
                type="button"
                className="bbm-linkbtn bbm-danger"
                onClick={() => handleRemove(item)}
                disabled={busyId === item.id}
              >
                Sil
              </button>
            </div>
          ))}
        </div>

        <aside className="bbm-cart-summary">
          <h3>Özet</h3>
          <div className="bbm-summary-row">
            <span>Ürün adedi</span>
            <span>{cart.totalItems}</span>
          </div>
          <div className="bbm-summary-row bbm-summary-total">
            <span>Toplam</span>
            <span>{formatPrice(cart.totalPrice)}</span>
          </div>
          <button type="button" className="bbm-btn" onClick={handleCheckout}>
            Ödemeye Geç
          </button>
          <button
            type="button"
            className="bbm-btn bbm-btn-ghost"
            onClick={() => setConfirmClear(true)}
          >
            Sepeti Boşalt
          </button>
        </aside>
      </div>

      <ConfirmModal
        open={confirmClear}
        title="Sepet boşaltılsın mı?"
        message="Sepetteki tüm ürünler çıkarılacak."
        confirmText="Evet, boşalt"
        busy={clearing}
        onConfirm={handleClear}
        onCancel={() => setConfirmClear(false)}
      />
    </section>
  );
}
