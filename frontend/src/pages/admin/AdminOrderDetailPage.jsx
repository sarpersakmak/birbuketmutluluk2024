import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { toast } from "react-toastify";

import ConfirmModal from "../../components/ConfirmModal";
import { fetchAdminOrder, updateOrderStatus } from "../../services/adminService";
import { formatDate, formatPrice } from "../../utils/format";
import { allowedNext, statusLabel } from "../../utils/orderStatus";

export default function AdminOrderDetailPage() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [nextStatus, setNextStatus] = useState("");
  const [saving, setSaving] = useState(false);
  const [confirmCancel, setConfirmCancel] = useState(false);

  const load = () => {
    setLoading(true);
    fetchAdminOrder(id)
      .then((res) => {
        setOrder(res.data);
        setNextStatus("");
      })
      .catch((err) => {
        if (err.response?.status === 404) setNotFound(true);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const applyStatus = async (status) => {
    setSaving(true);
    try {
      const res = await updateOrderStatus(id, status);
      setOrder(res.data);
      setNextStatus("");
      toast.success(`Durum güncellendi: ${statusLabel(status)}`);
    } catch (err) {
      toast.error(err.response?.data?.message || "Durum güncellenemedi.");
    } finally {
      setSaving(false);
      setConfirmCancel(false);
    }
  };

  if (loading) return <p className="bbm-status">Yükleniyor...</p>;
  if (notFound || !order) {
    return (
      <section>
        <h1>Sipariş bulunamadı</h1>
        <Link to="/admin/siparisler" className="bbm-btn">Siparişlere dön</Link>
      </section>
    );
  }

  const nexts = allowedNext(order.status);
  const nonCancelNexts = nexts.filter((s) => s !== "CANCELLED");
  const canCancel = nexts.includes("CANCELLED");
  const a = order.address;

  return (
    <section>
      <h1>Sipariş {order.orderNumber}</h1>
      <p className="bbm-muted">
        {formatDate(order.createdAt)} · Durum: <strong>{statusLabel(order.status)}</strong>
      </p>

      <div className="bbm-cart">
        <div className="bbm-cart-items">
          {order.items.map((it) => (
            <div className="bbm-cart-item" key={it.id}>
              <div className="bbm-cart-info">
                <span className="bbm-cart-name">{it.productName}</span>
                <span className="bbm-muted">{formatPrice(it.unitPrice)} × {it.quantity}</span>
              </div>
              <div className="bbm-cart-subtotal">{formatPrice(it.subtotal)}</div>
            </div>
          ))}
        </div>

        <aside className="bbm-cart-summary">
          <h3>Müşteri</h3>
          <div className="bbm-muted">{order.customerName}<br />{order.customerEmail}</div>

          <h3 style={{ marginTop: "1rem" }}>Teslimat Adresi</h3>
          {a ? (
            <div className="bbm-muted">
              <div><strong>{a.title}</strong></div>
              <div>{a.firstName} {a.lastName} {a.phone && `· ${a.phone}`}</div>
              <div>{a.addressDetail}, {a.neighborhood ? a.neighborhood + ", " : ""}{a.district}/{a.city} {a.postalCode}</div>
            </div>
          ) : <p className="bbm-muted">-</p>}

          {order.customerNote && (
            <>
              <h3 style={{ marginTop: "1rem" }}>Müşteri Notu</h3>
              <p className="bbm-muted">{order.customerNote}</p>
            </>
          )}

          <div className="bbm-summary-row bbm-summary-total">
            <span>Toplam</span><span>{formatPrice(order.totalPrice)}</span>
          </div>

          <h3 style={{ marginTop: "1rem" }}>Durum Güncelle</h3>
          {nonCancelNexts.length === 0 && !canCancel ? (
            <p className="bbm-muted">Bu sipariş için durum değişikliği yapılamaz.</p>
          ) : (
            <>
              {nonCancelNexts.length > 0 && (
                <div className="bbm-form-row" style={{ gap: "0.5rem" }}>
                  <select value={nextStatus} onChange={(e) => setNextStatus(e.target.value)}>
                    <option value="">Yeni durum seç...</option>
                    {nonCancelNexts.map((s) => (
                      <option key={s} value={s}>{statusLabel(s)}</option>
                    ))}
                  </select>
                  <button
                    type="button"
                    className="bbm-btn"
                    disabled={!nextStatus || saving}
                    onClick={() => applyStatus(nextStatus)}
                  >
                    Güncelle
                  </button>
                </div>
              )}
              {canCancel && (
                <button
                  type="button"
                  className="bbm-btn bbm-btn-danger"
                  disabled={saving}
                  onClick={() => setConfirmCancel(true)}
                >
                  Siparişi İptal Et
                </button>
              )}
            </>
          )}

          <Link to="/admin/siparisler" className="bbm-muted" style={{ textAlign: "center" }}>
            ← Siparişlere dön
          </Link>
        </aside>
      </div>

      <ConfirmModal
        open={confirmCancel}
        title="Sipariş iptal edilsin mi?"
        message="İptal edilince ürün stokları geri eklenecek. Bu işlem geri alınamaz."
        confirmText="Evet, iptal et"
        busy={saving}
        onConfirm={() => applyStatus("CANCELLED")}
        onCancel={() => setConfirmCancel(false)}
      />
    </section>
  );
}
