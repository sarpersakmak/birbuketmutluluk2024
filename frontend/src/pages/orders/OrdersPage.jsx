import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { fetchMyOrders } from "../../services/orderService";
import { formatDate, formatPrice } from "../../utils/format";
import { statusLabel } from "../../utils/orderStatus";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;
    fetchMyOrders()
      .then((res) => active && setOrders(res.data))
      .catch(() => active && setError(true))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, []);

  if (loading) return <p className="bbm-status">Siparişler yükleniyor...</p>;
  if (error) return <p className="bbm-status bbm-error">Siparişler yüklenemedi.</p>;

  return (
    <section className="bbm-page">
      <h1>Siparişlerim</h1>
      {orders.length === 0 ? (
        <div className="bbm-empty">
          <p>Henüz siparişiniz yok.</p>
          <Link to="/urunler" className="bbm-btn">Alışverişe başla</Link>
        </div>
      ) : (
        <table className="bbm-table">
          <thead>
            <tr>
              <th>Sipariş No</th>
              <th>Tarih</th>
              <th>Ürün</th>
              <th>Toplam</th>
              <th>Durum</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {orders.map((o) => (
              <tr key={o.id}>
                <td>{o.orderNumber}</td>
                <td>{formatDate(o.createdAt)}</td>
                <td>{o.itemCount}</td>
                <td>{formatPrice(o.totalPrice)}</td>
                <td>{statusLabel(o.status)}</td>
                <td>
                  <Link to={`/profile/siparislerim/${o.id}`} className="bbm-linkbtn">
                    Detay
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
