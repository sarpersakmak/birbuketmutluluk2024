import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { fetchOrder } from "../../services/orderService";
import { formatDate, formatPrice } from "../../utils/format";
import { statusLabel } from "../../utils/orderStatus";

const PLACEHOLDER = "https://placehold.co/80x80?text=-";

export default function OrderDetailPage() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    let active = true;
    setLoading(true);
    fetchOrder(id)
      .then((res) => active && setOrder(res.data))
      .catch((err) => {
        if (active && err.response?.status === 404) setNotFound(true);
      })
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [id]);

  if (loading) return <p className="bbm-status">Sipariş yükleniyor...</p>;
  if (notFound || !order) {
    return (
      <section className="bbm-page">
        <h1>Sipariş bulunamadı</h1>
        <Link to="/profile/siparislerim" className="bbm-btn">Siparişlerime dön</Link>
      </section>
    );
  }

  const a = order.address;

  return (
    <section className="bbm-page">
      <h1>Sipariş {order.orderNumber}</h1>
      <p className="bbm-muted">
        {formatDate(order.createdAt)} · Durum: <strong>{statusLabel(order.status)}</strong>
      </p>

      <div className="bbm-cart">
        <div className="bbm-cart-items">
          {order.items.map((it) => (
            <div className="bbm-cart-item" key={it.id}>
              <div className="bbm-cart-thumb">
                {it.productSlug ? (
                  <Link to={`/urunler/${it.productSlug}`}>
                    <img src={it.mainImageUrl || PLACEHOLDER} alt={it.productName}
                      onError={(e) => (e.currentTarget.src = PLACEHOLDER)} />
                  </Link>
                ) : (
                  <img src={it.mainImageUrl || PLACEHOLDER} alt={it.productName}
                    onError={(e) => (e.currentTarget.src = PLACEHOLDER)} />
                )}
              </div>
              <div className="bbm-cart-info">
                <span className="bbm-cart-name">{it.productName}</span>
                <span className="bbm-muted">
                  {formatPrice(it.unitPrice)} × {it.quantity}
                </span>
              </div>
              <div className="bbm-cart-subtotal">{formatPrice(it.subtotal)}</div>
            </div>
          ))}
        </div>

        <aside className="bbm-cart-summary">
          <h3>Teslimat Adresi</h3>
          {a ? (
            <div className="bbm-muted">
              <div><strong>{a.title}</strong></div>
              <div>{a.firstName} {a.lastName}</div>
              {a.phone && <div>{a.phone}</div>}
              <div>
                {a.addressDetail}, {a.neighborhood ? a.neighborhood + ", " : ""}
                {a.district}/{a.city} {a.postalCode}
              </div>
            </div>
          ) : (
            <p className="bbm-muted">-</p>
          )}

          {order.customerNote && (
            <>
              <h3 style={{ marginTop: "1rem" }}>Not</h3>
              <p className="bbm-muted">{order.customerNote}</p>
            </>
          )}

          <div className="bbm-summary-row bbm-summary-total">
            <span>Toplam</span>
            <span>{formatPrice(order.totalPrice)}</span>
          </div>
          <Link to="/profile/siparislerim" className="bbm-muted" style={{ textAlign: "center" }}>
            ← Siparişlerime dön
          </Link>
        </aside>
      </div>
    </section>
  );
}
