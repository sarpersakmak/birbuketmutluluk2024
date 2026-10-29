import { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import { Link, useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";

import WhatsAppButton from "../../components/WhatsAppButton";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";
import { fetchProductBySlug } from "../../services/productService";
import { formatPrice } from "../../utils/format";

const PLACEHOLDER = "https://placehold.co/600x600?text=Gorsel+Yok";

export default function ProductDetailPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const { addItem } = useCart();
  const [product, setProduct] = useState(null);
  const [activeIndex, setActiveIndex] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [adding, setAdding] = useState(false);
  const [lightbox, setLightbox] = useState(false);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setNotFound(false);

    fetchProductBySlug(slug)
      .then((res) => {
        if (!active) return;
        setProduct(res.data);
        setActiveIndex(0);
      })
      .catch((err) => {
        if (!active) return;
        if (err.response?.status === 404) setNotFound(true);
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [slug]);

  if (loading) return <p className="bbm-status">Ürün yükleniyor...</p>;

  if (notFound || !product) {
    return (
      <section className="bbm-page">
        <h1>Ürün bulunamadı</h1>
        <Link to="/urunler" className="bbm-btn">Ürünlere dön</Link>
      </section>
    );
  }

  const images = product.images && product.images.length > 0 ? product.images : [];
  const hasMultiple = images.length > 1;
  const activeUrl = images[activeIndex]?.imageUrl || PLACEHOLDER;

  const outOfStock = product.stockQuantity === 0;

  const step = (dir, e) => {
    if (e) e.stopPropagation();
    if (images.length === 0) return;
    setActiveIndex((i) => (i + dir + images.length) % images.length);
  };

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      toast.info("Sepete eklemek için giriş yapmalısınız.");
      navigate("/login", { state: { from: { pathname: `/urunler/${slug}` } } });
      return;
    }
    setAdding(true);
    try {
      await addItem(product.id, quantity);
      toast.success("Ürün sepete eklendi.");
    } catch (err) {
      toast.error(err.response?.data?.message || "Ürün sepete eklenemedi.");
    } finally {
      setAdding(false);
    }
  };

  return (
    <section className="bbm-page bbm-detail">
      <div className="bbm-detail-media">
        <div className="bbm-detail-main-wrap">
          <img
            className="bbm-detail-main"
            src={activeUrl}
            alt={product.name}
            onClick={() => images.length > 0 && setLightbox(true)}
            onError={(e) => {
              e.currentTarget.src = PLACEHOLDER;
            }}
          />
          {images.length > 0 && <span className="bbm-zoom-hint">🔍 Büyütmek için dokun</span>}
          {hasMultiple && (
            <>
              <button
                type="button"
                className="bbm-detail-nav bbm-detail-nav-prev"
                aria-label="Önceki görsel"
                onClick={(e) => step(-1, e)}
              >
                ‹
              </button>
              <button
                type="button"
                className="bbm-detail-nav bbm-detail-nav-next"
                aria-label="Sonraki görsel"
                onClick={(e) => step(1, e)}
              >
                ›
              </button>
            </>
          )}
        </div>

        {hasMultiple && (
          <div className="bbm-thumbs">
            {images.map((img, i) => (
              <button
                key={img.id ?? i}
                type="button"
                className={`bbm-thumb ${i === activeIndex ? "is-active" : ""}`}
                onClick={() => setActiveIndex(i)}
              >
                <img src={img.imageUrl} alt="" loading="lazy" />
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="bbm-detail-info">
        <h1>{product.name}</h1>
        <div className="bbm-detail-price">{formatPrice(product.price)}</div>

        <p className="bbm-detail-desc">{product.description}</p>

        <ul className="bbm-list">
          <li>
            <strong>Durum:</strong>{" "}
            {outOfStock ? "Tükendi" : product.available ? "Satışta" : "Satışta Değil"}
          </li>
          {product.productionTime && (
            <li><strong>Üretim Süresi:</strong> {product.productionTime}</li>
          )}
          {product.color && <li><strong>Renk:</strong> {product.color}</li>}
          {product.size && <li><strong>Boyut:</strong> {product.size}</li>}
          {product.customizable && <li><strong>Kişiselleştirilebilir:</strong> Evet</li>}
        </ul>

        <div className="bbm-detail-actions">
          <label className="bbm-qty">
            Adet
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(Math.max(1, Number(e.target.value) || 1))}
              disabled={outOfStock}
            />
          </label>
          <button
            type="button"
            className="bbm-btn"
            onClick={handleAddToCart}
            disabled={outOfStock || !product.available || adding}
          >
            {outOfStock ? "Tükendi" : adding ? "Ekleniyor..." : "Sepete Ekle"}
          </button>
        </div>

        <div className="bbm-detail-wa">
          <WhatsAppButton
            text="WhatsApp ile Sipariş Ver"
            message={`Merhaba, "${product.name}" ürünü hakkında bilgi almak / sipariş vermek istiyorum.`}
          />
        </div>

        <Link to="/urunler" className="bbm-muted bbm-back">← Ürünlere dön</Link>
      </div>

      {/* Büyük görsel (lightbox) — body'ye portal, en üst katman */}
      {lightbox && createPortal(
        <div className="bbm-lightbox" onClick={() => setLightbox(false)}>
          <button type="button" className="bbm-lightbox-close" aria-label="Kapat">✕</button>
          {hasMultiple && (
            <button
              type="button"
              className="bbm-lightbox-nav bbm-lightbox-prev"
              aria-label="Önceki"
              onClick={(e) => step(-1, e)}
            >
              ‹
            </button>
          )}
          <img
            className="bbm-lightbox-img"
            src={activeUrl}
            alt={product.name}
            onClick={(e) => e.stopPropagation()}
            onError={(e) => {
              e.currentTarget.src = PLACEHOLDER;
            }}
          />
          {hasMultiple && (
            <button
              type="button"
              className="bbm-lightbox-nav bbm-lightbox-next"
              aria-label="Sonraki"
              onClick={(e) => step(1, e)}
            >
              ›
            </button>
          )}
        </div>,
        document.body
      )}
    </section>
  );
}
