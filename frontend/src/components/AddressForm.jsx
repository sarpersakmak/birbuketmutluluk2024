import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { getDistricts, getProvinces } from "../services/locationService";

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
  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);

  // İlleri bir kez yükle.
  useEffect(() => {
    getProvinces()
      .then((res) => setProvinces(res.data))
      .catch(() => toast.error("İl listesi yüklenemedi."));
  }, []);

  // İl değişince (veya iller yüklenince) o ilin ilçelerini getir.
  useEffect(() => {
    const p = provinces.find((x) => x.name === form.city);
    if (p) {
      getDistricts(p.id)
        .then((res) => setDistricts(res.data))
        .catch(() => setDistricts([]));
    } else {
      setDistricts([]);
    }
  }, [form.city, provinces]);

  const change = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => {
      const next = { ...prev, [name]: type === "checkbox" ? checked : value };
      if (name === "city") next.district = ""; // il değişince ilçeyi sıfırla
      return next;
    });
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
    <form className="bbm-admin-form bbm-address-form" onSubmit={handleSubmit}>
      <h3 className="bbm-address-form-title">Adres Bilgileri</h3>

      <div className="bbm-form-grid">
        <label className="bbm-field">
          <span className="bbm-field-label">Adres Başlığı</span>
          <input name="title" placeholder="Örn. Ev, İş" value={form.title} onChange={change} />
        </label>
        <label className="bbm-field">
          <span className="bbm-field-label">Telefon</span>
          <input name="phone" placeholder="05xx xxx xx xx" value={form.phone} onChange={change} />
        </label>

        <label className="bbm-field">
          <span className="bbm-field-label">Alıcı Adı</span>
          <input name="firstName" placeholder="Ad" value={form.firstName} onChange={change} />
        </label>
        <label className="bbm-field">
          <span className="bbm-field-label">Alıcı Soyadı</span>
          <input name="lastName" placeholder="Soyad" value={form.lastName} onChange={change} />
        </label>

        <label className="bbm-field">
          <span className="bbm-field-label">İl</span>
          <select name="city" value={form.city} onChange={change}>
            <option value="">İl seçin</option>
            {form.city && !provinces.some((p) => p.name === form.city) && (
              <option value={form.city}>{form.city}</option>
            )}
            {provinces.map((p) => (
              <option key={p.id} value={p.name}>{p.name}</option>
            ))}
          </select>
        </label>

        <label className="bbm-field">
          <span className="bbm-field-label">İlçe</span>
          <select name="district" value={form.district} onChange={change} disabled={!form.city}>
            <option value="">{form.city ? "İlçe seçin" : "Önce il seçin"}</option>
            {form.district && !districts.some((d) => d.name === form.district) && (
              <option value={form.district}>{form.district}</option>
            )}
            {districts.map((d) => (
              <option key={d.id} value={d.name}>{d.name}</option>
            ))}
          </select>
        </label>

        <label className="bbm-field">
          <span className="bbm-field-label">Mahalle</span>
          <input name="neighborhood" placeholder="Mahalle" value={form.neighborhood} onChange={change} />
        </label>
        <label className="bbm-field">
          <span className="bbm-field-label">Posta Kodu</span>
          <input name="postalCode" placeholder="34xxx" value={form.postalCode} onChange={change} />
        </label>
      </div>

      <label className="bbm-field">
        <span className="bbm-field-label">Açık Adres</span>
        <textarea
          name="addressDetail"
          placeholder="Cadde, sokak, bina/daire no..."
          value={form.addressDetail}
          onChange={change}
        />
      </label>

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
