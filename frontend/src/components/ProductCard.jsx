import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { formatPrice } from "../utils/format";

const PLACEHOLDER = "https://placehold.co/600x600?text=Gorsel+Yok";

export default function ProductCard({ product }) {
  const { isAuthenticated } = useAuth();
  const { addItem } = useCart();
  const navigate = useNavigate();
  const [adding, setAdding] = useState(false);
  const [idx, setIdx] = useState(0);

  const outOfStock = product.stockQuantity === 0;
  const disabled = outOfStock || !product.available;

  // Görsel galerisi: tüm açılar (yoksa ana görsel, o da yoksa placeholder)
  const images =
    product.imageUrls && product.imageUrls.length > 0
      ? product.imageUrls
      : product.mainImageUrl
        ? [product.mainImageUrl]
        : [PLACEHOLDER];
  const hasMultiple = images.length > 1;
  const current = images[idx] || PLACEHOLDER;

  // Oklar kartın <Link>'ine tıklamayı tetiklemesin.
  const step = (e, dir) => {
    e.preventDefault();
    e.stopPropagation();
    setIdx((i) => (i + dir + images.length) % images.length);
  };

  const handleQuickAdd = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!isAuthenticated) {
      toast.info("Sepete eklemek için giriş yapmalısınız.");
      navigate("/login");
      return;
    }
    setAdding(true);
    try {
      await addItem(product.id, 1);
      toast.success("Ürün sepete eklendi.");
    } catch (err) {
      toast.error(err.response?.data?.message || "Ürün sepete eklenemedi.");
    } finally {
      setAdding(false);
    }
  };

  return (
    <Link to={`/urunler/${product.slug}`} className="bbm-card">
      <div className="bbm-card-media">
        <img
          src={current}
          alt={product.name}
          loading="lazy"
          onError={(e) => {
            e.currentTarget.src = PLACEHOLDER;
          }}
        />

        {outOfStock && <span className="bbm-badge bbm-badge-out">Tükendi</span>}
        {!outOfStock && !product.available && (
          <span className="bbm-badge bbm-badge-muted">Satışta Değil</span>
        )}

        {hasMultiple && (
          <>
            <button
              type="button"
              className="bbm-card-nav bbm-card-nav-prev"
              aria-label="Önceki görsel"
              onClick={(e) => step(e, -1)}
            >
              ‹
            </button>
            <button
              type="button"
              className="bbm-card-nav bbm-card-nav-next"
              aria-label="Sonraki görsel"
              onClick={(e) => step(e, 1)}
            >
              ›
            </button>
            <span className="bbm-card-dots">
              {images.map((_, i) => (
                <i key={i} className={i === idx ? "is-active" : ""} />
              ))}
            </span>
          </>
        )}
      </div>

      <div className="bbm-card-body">
        <h3 className="bbm-card-title">{product.name}</h3>
        <span className="bbm-card-price">{formatPrice(product.price)}</span>
        <button
          type="button"
          className="bbm-btn bbm-card-add"
          onClick={handleQuickAdd}
          disabled={disabled || adding}
        >
          {outOfStock ? "Tükendi" : adding ? "Ekleniyor..." : "+ Sepete Ekle"}
        </button>
      </div>
    </Link>
  );
}
