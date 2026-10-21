import { useState } from "react";
import { toast } from "react-toastify";

const EMPTY = {
  title: "",
  firstName: "",
  lastName: "",
  phone: "",
  city: "",
  district: "",
  neighborhood: "",
  addressDetail: "",
  postalCode: "",
  isDefault: false,
};

export default function AddressForm({ initial, onSubmit, onCancel, submitting }) {
  const [form, setForm] = useState({ ...EMPTY, ...(initial || {}) });

  const change = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === "checkbox" ? checked : value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const required = ["title", "firstName", "lastName", "city", "district", "addressDetail"];
    for (const f of required) {
      if (!String(form[f]).trim()) {
        toast.error("Lütfen zorunlu alanları doldurun.");
        return;
      }
    }
    onSubmit(form);
  };

  return (
    <form className="bbm-admin-form" onSubmit={handleSubmit}>
      <div className="bbm-form-row">
        <input name="title" placeholder="Adres başlığı (ör. Ev)" value={form.title} onChange={change} />
        <input name="phone" placeholder="Telefon" value={form.phone} onChange={change} />
      </div>
      <div className="bbm-form-row">
        <input name="firstName" placeholder="Ad" value={form.firstName} onChange={change} />
        <input name="lastName" placeholder="Soyad" value={form.lastName} onChange={change} />
      </div>
      <div className="bbm-form-row">
        <input name="city" placeholder="İl" value={form.city} onChange={change} />
        <input name="district" placeholder="İlçe" value={form.district} onChange={change} />
        <input name="neighborhood" placeholder="Mahalle" value={form.neighborhood} onChange={change} />
      </div>
      <textarea name="addressDetail" placeholder="Açık adres" value={form.addressDetail} onChange={change} />
      <div className="bbm-form-row">
        <input name="postalCode" placeholder="Posta kodu" value={form.postalCode} onChange={change} />
      </div>
      <label className="bbm-check">
        <input type="checkbox" name="isDefault" checked={form.isDefault} onChange={change} />
        Varsayılan adres yap
      </label>
      <div className="bbm-form-actions">
        <button type="submit" className="bbm-btn" disabled={submitting}>
          {submitting ? "Kaydediliyor..." : "Adresi Kaydet"}
        </button>
        {onCancel && (
          <button type="button" className="bbm-btn bbm-btn-ghost" onClick={onCancel}>
            Vazgeç
          </button>
        )}
      </div>
    </form>
  );
}
