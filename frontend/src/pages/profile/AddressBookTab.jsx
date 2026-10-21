import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import AddressForm from "../../components/AddressForm";
import ConfirmModal from "../../components/ConfirmModal";
import {
  createAddress,
  deleteAddress,
  fetchAddresses,
  updateAddress,
} from "../../services/addressService";

export default function AddressBookTab() {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [mode, setMode] = useState(null); // null | "new" | <id>
  const [saving, setSaving] = useState(false);
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = () => {
    setLoading(true);
    fetchAddresses()
      .then((res) => setAddresses(res.data))
      .catch(() => toast.error("Adresler yüklenemedi."))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleSubmit = async (data) => {
    setSaving(true);
    try {
      if (mode === "new") {
        await createAddress(data);
        toast.success("Adres eklendi.");
      } else {
        await updateAddress(mode, data);
        toast.success("Adres güncellendi.");
      }
      setMode(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "İşlem başarısız.");
    } finally {
      setSaving(false);
    }
  };

  const makeDefault = async (a) => {
    try {
      await updateAddress(a.id, { ...a, isDefault: true });
      toast.success("Varsayılan adres güncellendi.");
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "İşlem başarısız.");
    }
  };

  const confirmDelete = async () => {
    setDeleting(true);
    try {
      await deleteAddress(toDelete.id);
      toast.info("Adres silindi.");
      setToDelete(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "Adres silinemedi.");
      setToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  if (loading) return <p className="bbm-status">Yükleniyor...</p>;

  return (
    <div className="bbm-tab-panel">
      <div className="bbm-tab-header">
        <h3>Adreslerim</h3>
        {mode === null && (
          <button type="button" className="bbm-btn bbm-btn-ghost" onClick={() => setMode("new")}>
            + Yeni adres
          </button>
        )}
      </div>

      {mode === "new" && (
        <AddressForm onSubmit={handleSubmit} onCancel={() => setMode(null)} submitting={saving} />
      )}

      {addresses.length === 0 && mode !== "new" ? (
        <p className="bbm-muted">Henüz kayıtlı adresiniz yok.</p>
      ) : (
        <div className="bbm-address-list">
          {addresses.map((a) =>
            mode === a.id ? (
              <AddressForm
                key={a.id}
                initial={a}
                onSubmit={handleSubmit}
                onCancel={() => setMode(null)}
                submitting={saving}
              />
            ) : (
              <div key={a.id} className="bbm-address-card">
                <div style={{ flex: 1 }}>
                  <strong>{a.title}</strong>{" "}
                  {a.isDefault && <span className="bbm-chip">Varsayılan</span>}
                  <div className="bbm-muted">{a.firstName} {a.lastName} · {a.phone}</div>
                  <div className="bbm-muted">
                    {a.addressDetail}, {a.neighborhood ? a.neighborhood + ", " : ""}
                    {a.district}/{a.city} {a.postalCode}
                  </div>
                  <div className="bbm-row-actions" style={{ marginTop: "0.5rem" }}>
                    <button type="button" className="bbm-linkbtn" onClick={() => setMode(a.id)}>
                      Düzenle
                    </button>
                    {!a.isDefault && (
                      <button type="button" className="bbm-linkbtn" onClick={() => makeDefault(a)}>
                        Varsayılan yap
                      </button>
                    )}
                    <button
                      type="button"
                      className="bbm-linkbtn bbm-danger"
                      onClick={() => setToDelete(a)}
                    >
                      Sil
                    </button>
                  </div>
                </div>
              </div>
            )
          )}
        </div>
      )}

      <ConfirmModal
        open={Boolean(toDelete)}
        title="Adres silinsin mi?"
        message={toDelete ? `"${toDelete.title}" adresi silinecek.` : ""}
        busy={deleting}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}
