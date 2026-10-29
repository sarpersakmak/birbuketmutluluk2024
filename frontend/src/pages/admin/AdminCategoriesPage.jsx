import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import ConfirmModal from "../../components/ConfirmModal";
import {
  createCategory,
  deleteCategory,
  fetchAllCategories,
  updateCategory,
} from "../../services/categoryService";

const EMPTY_FORM = { name: "", description: "", imageUrl: "", active: true };

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = () => {
    setLoading(true);
    fetchAllCategories()
      .then((res) => setCategories(res.data))
      .catch(() => toast.error("Kategoriler yüklenemedi."))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingId(null);
  };

  const startEdit = (c) => {
    setEditingId(c.id);
    setForm({
      name: c.name,
      description: c.description || "",
      imageUrl: c.imageUrl || "",
      active: c.active,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim()) {
      toast.error("Kategori adı zorunludur.");
      return;
    }
    setSaving(true);
    try {
      if (editingId) {
        await updateCategory(editingId, form);
        toast.success("Kategori güncellendi.");
      } else {
        await createCategory(form);
        toast.success("Kategori eklendi.");
      }
      resetForm();
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "İşlem başarısız.");
    } finally {
      setSaving(false);
    }
  };

  const confirmDelete = async () => {
    setDeleting(true);
    try {
      await deleteCategory(toDelete.id);
      toast.success("Kategori silindi.");
      setToDelete(null);
      load();
    } catch (err) {
      // 409: içinde ürün var -> pasife alma önerisi (backend mesajı gösterilir)
      toast.error(err.response?.data?.message || "Kategori silinemedi.");
      setToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  return (
    <section className="bbm-page">
      <h1>Kategori Yönetimi</h1>

      <form className="bbm-admin-form" onSubmit={handleSubmit}>
        <h3>{editingId ? "Kategori Düzenle" : "Yeni Kategori"}</h3>
        <div className="bbm-form-row">
          <label className="bbm-field">
            <span className="bbm-field-label">Kategori Adı</span>
            <input
              type="text"
              placeholder="Örn. Buketler"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
            />
          </label>
          <label className="bbm-field">
            <span className="bbm-field-label">Görsel Adresi (opsiyonel)</span>
            <input
              type="text"
              placeholder="https://..."
              value={form.imageUrl}
              onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
            />
          </label>
        </div>
        <label className="bbm-field">
          <span className="bbm-field-label">Açıklama (opsiyonel)</span>
          <textarea
            placeholder="Kategori açıklaması"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />
        </label>
        <label className="bbm-check">
          <input
            type="checkbox"
            checked={form.active}
            onChange={(e) => setForm({ ...form, active: e.target.checked })}
          />
          Aktif
        </label>
        <div className="bbm-form-actions">
          <button type="submit" className="bbm-btn" disabled={saving}>
            {saving ? "Kaydediliyor..." : editingId ? "Güncelle" : "Ekle"}
          </button>
          {editingId && (
            <button type="button" className="bbm-btn bbm-btn-ghost" onClick={resetForm}>
              Vazgeç
            </button>
          )}
        </div>
      </form>

      {loading ? (
        <p className="bbm-status">Yükleniyor...</p>
      ) : (
        <table className="bbm-table">
          <thead>
            <tr>
              <th>Ad</th>
              <th>Slug</th>
              <th>Durum</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((c) => (
              <tr key={c.id}>
                <td>{c.name}</td>
                <td>{c.slug}</td>
                <td>{c.active ? "Aktif" : "Pasif"}</td>
                <td className="bbm-row-actions">
                  <button type="button" className="bbm-linkbtn" onClick={() => startEdit(c)}>
                    Düzenle
                  </button>
                  <button
                    type="button"
                    className="bbm-linkbtn bbm-danger"
                    onClick={() => setToDelete(c)}
                  >
                    Sil
                  </button>
                </td>
              </tr>
            ))}
            {categories.length === 0 && (
              <tr>
                <td colSpan={4} className="bbm-muted">Henüz kategori yok.</td>
              </tr>
            )}
          </tbody>
        </table>
      )}

      <ConfirmModal
        open={Boolean(toDelete)}
        title="Kategori silinsin mi?"
        message={toDelete ? `"${toDelete.name}" kategorisi silinecek.` : ""}
        busy={deleting}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </section>
  );
}
