import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import AddressForm from "../../components/AddressForm";
import { useCart } from "../../context/CartContext";
import { createAddress, fetchAddresses } from "../../services/addressService";
import { createOrder } from "../../services/orderService";
import { formatPrice } from "../../utils/format";

export default function CheckoutPage() {
  const { cart, refresh } = useCart();
  const navigate = useNavigate();

  const [addresses, setAddresses] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [savingAddress, setSavingAddress] = useState(false);
  const [note, setNote] = useState("");
  const [placing, setPlacing] = useState(false);

  const loadAddresses = async (selectNewest = false) => {
    const res = await fetchAddresses();
    setAddresses(res.data);
    if (res.data.length === 0) {
      setShowForm(true);
      setSelectedId(null);
    } else if (selectNewest) {
      setSelectedId(res.data[res.data.length - 1].id);
      setShowForm(false);
    } else {
      const def = res.data.find((a) => a.isDefault) || res.data[0];
      setSelectedId(def.id);
    }
  };

  useEffect(() => {
    loadAddresses().catch(() => toast.error("Adresler yüklenemedi."));
  }, []);

  const items = cart?.items || [];

  const handleAddAddress = async (data) => {
    setSavingAddress(true);
    try {
      await createAddress(data);
      toast.success("Adres eklendi.");
      await loadAddresses(true);
    } catch (err) {
      toast.error(err.response?.data?.message || "Adres eklenemedi.");
    } finally {
      setSavingAddress(false);
    }
  };

  const handlePlaceOrder = async () => {
    if (!selectedId) {
      toast.error("Lütfen bir teslimat adresi seçin.");
      return;
    }
    setPlacing(true);
    try {
      const res = await createOrder({ addressId: selectedId, customerNote: note });
      await refresh(); // sepet boşaldı
      toast.success(`Siparişiniz oluşturuldu. No: ${res.data.orderNumber}`);
      navigate(`/profile/siparislerim/${res.data.id}`, { replace: true });
    } catch (err) {
      toast.error(err.response?.data?.message || "Sipariş oluşturulamadı.");
    } finally {
      setPlacing(false);
    }
  };

  if (items.length === 0) {
    return (
      <section className="bbm-page">
        <h1>Ödeme</h1>
        <div className="bbm-empty">
          <p>Sepetiniz boş olduğu için ödeme yapılamaz.</p>
          <Link to="/urunler" className="bbm-btn">Ürünleri keşfet</Link>
        </div>
      </section>
    );
  }

  return (
    <section className="bbm-page">
      <h1>Ödeme</h1>
      <div className="bbm-cart">
        <div>
          {/* Adres seçimi */}
          <h3>Teslimat Adresi</h3>
          {addresses.length > 0 && (
            <div className="bbm-address-list">
              {addresses.map((a) => (
                <label key={a.id} className={`bbm-address-card ${selectedId === a.id ? "is-selected" : ""}`}>
                  <input
                    type="radio"
                    name="address"
                    checked={selectedId === a.id}
                    onChange={() => setSelectedId(a.id)}
                  />
                  <div>
                    <strong>{a.title}</strong> {a.isDefault && <span className="bbm-chip">Varsayılan</span>}
                    <div className="bbm-muted">
                      {a.firstName} {a.lastName} · {a.phone}
                    </div>
                    <div className="bbm-muted">
                      {a.addressDetail}, {a.neighborhood ? a.neighborhood + ", " : ""}
                      {a.district}/{a.city} {a.postalCode}
                    </div>
                  </div>
                </label>
              ))}
            </div>
          )}

          {showForm ? (
            <AddressForm
              onSubmit={handleAddAddress}
              onCancel={addresses.length > 0 ? () => setShowForm(false) : undefined}
              submitting={savingAddress}
            />
          ) : (
            <button type="button" className="bbm-btn bbm-btn-ghost" onClick={() => setShowForm(true)}>
              + Yeni adres ekle
            </button>
          )}

          {/* Not */}
          <h3 style={{ marginTop: "1.5rem" }}>Sipariş Notu (opsiyonel)</h3>
          <textarea
            className="bbm-note"
            placeholder="Örn. Pastel tonlarda hazırlanmasını istiyorum."
            value={note}
            onChange={(e) => setNote(e.target.value)}
            maxLength={1000}
          />
        </div>

        {/* Özet */}
        <aside className="bbm-cart-summary">
          <h3>Sipariş Özeti</h3>
          {items.map((it) => (
            <div className="bbm-summary-row" key={it.id}>
              <span>
                {it.productName} × {it.quantity}
              </span>
              <span>{formatPrice(it.subtotal)}</span>
            </div>
          ))}
          <div className="bbm-summary-row bbm-summary-total">
            <span>Toplam</span>
            <span>{formatPrice(cart.totalPrice)}</span>
          </div>
          <button type="button" className="bbm-btn" onClick={handlePlaceOrder} disabled={placing}>
            {placing ? "Oluşturuluyor..." : "Siparişi Oluştur"}
          </button>
          <Link to="/cart" className="bbm-muted" style={{ textAlign: "center" }}>
            ← Sepete dön
          </Link>
        </aside>
      </div>
    </section>
  );
}
