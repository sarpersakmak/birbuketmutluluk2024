import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";

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
  const [activeImage, setActiveImage] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setNotFound(false);

    fetchProductBySlug(slug)
      .then((res) => {
        if (!active) return;
        setProduct(res.data);
        const main = res.data.images?.find((i) => i.isMain) || res.data.images?.[0];
        setActiveImage(main?.imageUrl || null);
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

  const outOfStock = product.stockQuantity === 0;

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
        <img
          className="bbm-detail-main"
          src={activeImage || PLACEHOLDER}
          alt={product.name}
          onError={(e) => {
            e.currentTarget.src = PLACEHOLDER;
          }}
        />
        {product.images?.length > 1 && (
          <div className="bbm-thumbs">
            {product.images.map((img) => (
              <button
                key={img.id}
                type="button"
                className={`bbm-thumb ${activeImage === img.imageUrl ? "is-active" : ""}`}
                onClick={() => setActiveImage(img.imageUrl)}
              >
                <img src={img.imageUrl} alt="" loading="lazy" />
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="bbm-detail-info">
        <span className="bbm-card-cat">{product.categoryName}</span>
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

        <Link to="/urunler" className="bbm-muted bbm-back">← Ürünlere dön</Link>
      </div>
    </section>
  );
}
