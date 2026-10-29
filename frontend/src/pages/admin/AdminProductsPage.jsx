import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import ConfirmModal from "../../components/ConfirmModal";
import {
  createProduct,
  deleteProduct,
  fetchProductBySlug,
  fetchProducts,
  updateProduct,
} from "../../services/productService";
import { uploadImage } from "../../services/uploadService";
import { formatPrice } from "../../utils/format";

const EMPTY_FORM = {
  name: "",
  categoryId: "",
  description: "",
  price: "",
  stockQuantity: "",
  productionTime: "",
  color: "",
  size: "",
  customizable: false,
  featured: false,
  available: true,
  images: [{ imageUrl: "", isMain: true }],
};

export default function AdminProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [uploadingIdx, setUploadingIdx] = useState(null);

  const loadProducts = () => {
    setLoading(true);
    fetchProducts({ size: 60, sort: "newest" })
      .then((res) => setProducts(res.data.content))
      .catch(() => toast.error("Ürünler yüklenemedi."))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingId(null);
  };

  const startEdit = async (summary) => {
    try {
      const res = await fetchProductBySlug(summary.slug);
      const p = res.data;
      setEditingId(p.id);
      setForm({
        name: p.name,
        categoryId: String(p.categoryId),
        description: p.description || "",
        price: String(p.price),
        stockQuantity: String(p.stockQuantity),
        productionTime: p.productionTime || "",
        color: p.color || "",
        size: p.size || "",
        customizable: p.customizable,
        featured: p.featured,
        available: p.available,
        images:
          p.images && p.images.length > 0
            ? p.images.map((i) => ({ imageUrl: i.imageUrl, isMain: i.isMain }))
            : [{ imageUrl: "", isMain: true }],
      });
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch {
      toast.error("Ürün bilgisi getirilemedi.");
    }
  };

  // ---- görsel satırları ----
  const updateImage = (index, url) => {
    const images = form.images.map((img, i) => (i === index ? { ...img, imageUrl: url } : img));
    setForm({ ...form, images });
  };
  const setMainImage = (index) => {
    const images = form.images.map((img, i) => ({ ...img, isMain: i === index }));
    setForm({ ...form, images });
  };
  const addImageRow = () => setForm({ ...form, images: [...form.images, { imageUrl: "", isMain: false }] });
  const removeImageRow = (index) => {
    let images = form.images.filter((_, i) => i !== index);
    if (images.length === 0) images = [{ imageUrl: "", isMain: true }];
    if (!images.some((i) => i.isMain)) images[0].isMain = true;
    setForm({ ...form, images });
  };
  const handleFileUpload = async (index, file) => {
    if (!file) return;
    setUploadingIdx(index);
    try {
      const res = await uploadImage(file);
      updateImage(index, res.data.url);
      toast.success("Görsel yüklendi.");
    } catch (err) {
      toast.error(err.response?.data?.message || "Görsel yüklenemedi.");
    } finally {
      setUploadingIdx(null);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim()) return toast.error("Ürün adı zorunludur.");
    if (!form.price || Number(form.price) <= 0) return toast.error("Fiyat 0'dan büyük olmalıdır.");
    if (form.stockQuantity === "" || Number(form.stockQuantity) < 0)
      return toast.error("Geçerli bir stok giriniz.");

    const payload = {
      name: form.name.trim(),
      categoryId: null,
      description: form.description,
      price: Number(form.price),
      stockQuantity: Number(form.stockQuantity),
      productionTime: form.productionTime,
      color: form.color,
      size: form.size,
      customizable: form.customizable,
      featured: form.featured,
      available: form.available,
      images: form.images
        .filter((i) => i.imageUrl.trim())
        .map((i, idx) => ({ imageUrl: i.imageUrl.trim(), displayOrder: idx, isMain: i.isMain })),
    };

    setSaving(true);
    try {
      if (editingId) {
        await updateProduct(editingId, payload);
        toast.success("Ürün güncellendi.");
      } else {
        await createProduct(payload);
        toast.success("Ürün eklendi.");
      }
      resetForm();
      loadProducts();
    } catch (err) {
      toast.error(err.response?.data?.message || "İşlem başarısız.");
    } finally {
      setSaving(false);
    }
  };

  const confirmDelete = async () => {
    setDeleting(true);
    try {
      const res = await deleteProduct(toDelete.id);
      if (res.data && res.data.hardDeleted === false) {
        toast.info(res.data.message);
      } else {
        toast.success(res.data?.message || "Ürün silindi.");
      }
      setToDelete(null);
      loadProducts();
    } catch (err) {
      toast.error(err.response?.data?.message || "Ürün silinemedi.");
      setToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  return (
    <section className="bbm-page">
      <h1>Ürün Yönetimi</h1>

      <form className="bbm-admin-form" onSubmit={handleSubmit}>
        <h3>{editingId ? "Ürün Düzenle" : "Yeni Ürün"}</h3>

        <label className="bbm-field">
          <span className="bbm-field-label">Ürün Adı</span>
          <input
            type="text"
            placeholder="Örn. Soğuk Porselen Gül Panosu"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
        </label>

        <label className="bbm-field">
          <span className="bbm-field-label">Açıklama</span>
          <textarea
            placeholder="Ürün açıklaması"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />
        </label>

        <div className="bbm-form-row">
          <label className="bbm-field">
            <span className="bbm-field-label">Fiyat (₺)</span>
            <input
              type="number"
              step="0.01"
              min="0"
              placeholder="0,00"
              value={form.price}
              onChange={(e) => setForm({ ...form, price: e.target.value })}
            />
          </label>
          <label className="bbm-field">
            <span className="bbm-field-label">Stok</span>
            <input
              type="number"
              min="0"
              placeholder="Adet"
              value={form.stockQuantity}
              onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })}
            />
          </label>
        </div>

        <div className="bbm-form-row">
          <label className="bbm-field">
            <span className="bbm-field-label">Üretim Süresi</span>
            <input
              type="text"
              placeholder="Örn. 3-5 gün"
              value={form.productionTime}
              onChange={(e) => setForm({ ...form, productionTime: e.target.value })}
            />
          </label>
          <label className="bbm-field">
            <span className="bbm-field-label">Renk</span>
            <input
              type="text"
              placeholder="Örn. Pudra"
              value={form.color}
              onChange={(e) => setForm({ ...form, color: e.target.value })}
            />
          </label>
          <label className="bbm-field">
            <span className="bbm-field-label">Boyut</span>
            <input
              type="text"
              placeholder="Örn. Orta"
              value={form.size}
              onChange={(e) => setForm({ ...form, size: e.target.value })}
            />
          </label>
        </div>

        <div className="bbm-check-row">
          <label className="bbm-check">
            <input
              type="checkbox"
              checked={form.customizable}
              onChange={(e) => setForm({ ...form, customizable: e.target.checked })}
            />
            Kişiselleştirilebilir
          </label>
          <label className="bbm-check">
            <input
              type="checkbox"
              checked={form.featured}
              onChange={(e) => setForm({ ...form, featured: e.target.checked })}
            />
            Öne Çıkan
          </label>
          <label className="bbm-check">
            <input
              type="checkbox"
              checked={form.available}
              onChange={(e) => setForm({ ...form, available: e.target.checked })}
            />
            Satışta (Aktif)
          </label>
        </div>

        <div className="bbm-images-editor">
          <strong>Görseller</strong>
          <small className="bbm-muted">Bilgisayardan yükle ya da bir görsel adresi (URL) yapıştır.</small>
          {form.images.map((img, index) => (
            <div className="bbm-image-row" key={index}>
              {img.imageUrl ? (
                <img className="bbm-image-preview" src={img.imageUrl} alt="" />
              ) : (
                <span className="bbm-image-preview bbm-image-preview-empty">—</span>
              )}
              <input
                type="text"
                placeholder="https://... veya dosya yükle"
                value={img.imageUrl}
                onChange={(e) => updateImage(index, e.target.value)}
              />
              <label className="bbm-btn bbm-btn-ghost bbm-btn-sm bbm-file-btn">
                {uploadingIdx === index ? "Yükleniyor..." : "Dosya Seç"}
                <input
                  type="file"
                  accept="image/jpeg,image/png,image/webp"
                  hidden
                  disabled={uploadingIdx === index}
                  onChange={(e) => {
                    handleFileUpload(index, e.target.files?.[0]);
                    e.target.value = "";
                  }}
                />
              </label>
              <label className="bbm-check">
                <input
                  type="radio"
                  name="mainImage"
                  checked={img.isMain}
                  onChange={() => setMainImage(index)}
                />
                Ana
              </label>
              <button type="button" className="bbm-linkbtn bbm-danger" onClick={() => removeImageRow(index)}>
                Kaldır
              </button>
            </div>
          ))}
          <button type="button" className="bbm-btn bbm-btn-ghost" onClick={addImageRow}>
            + Görsel ekle
          </button>
        </div>

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
              <th>Fiyat</th>
              <th>Stok</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id}>
                <td>{p.name}</td>
                <td>{formatPrice(p.price)}</td>
                <td>{p.stockQuantity === 0 ? "Tükendi" : p.stockQuantity}</td>
                <td className="bbm-row-actions">
                  <button type="button" className="bbm-linkbtn" onClick={() => startEdit(p)}>
                    Düzenle
                  </button>
                  <button
                    type="button"
                    className="bbm-linkbtn bbm-danger"
                    onClick={() => setToDelete(p)}
                  >
                    Sil
                  </button>
                </td>
              </tr>
            ))}
            {products.length === 0 && (
              <tr>
                <td colSpan={4} className="bbm-muted">Henüz ürün yok.</td>
              </tr>
            )}
          </tbody>
        </table>
      )}

      <ConfirmModal
        open={Boolean(toDelete)}
        title="Ürün silinsin mi?"
        message={toDelete ? `"${toDelete.name}" ürünü silinecek.` : ""}
        busy={deleting}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </section>
  );
}
