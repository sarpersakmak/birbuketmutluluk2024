import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { fetchUser, fetchUsers, updateUserStatus } from "../../services/adminService";
import { formatDate, formatPrice } from "../../utils/format";
import { statusLabel } from "../../utils/orderStatus";

export default function AdminUsersPage() {
  const [users, setUsers] = useState([]);
  const [q, setQ] = useState("");
  const [loading, setLoading] = useState(true);
  const [detail, setDetail] = useState(null); // AdminUserDetailResponse

  const load = (query) => {
    setLoading(true);
    fetchUsers(query)
      .then((res) => setUsers(res.data))
      .catch(() => toast.error("Kullanıcılar yüklenemedi."))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load("");
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    load(q.trim());
  };

  const toggleActive = async (u) => {
    try {
      await updateUserStatus(u.id, !u.active);
      toast.success(u.active ? "Kullanıcı pasife alındı." : "Kullanıcı aktifleştirildi.");
      load(q.trim());
      if (detail && detail.user.id === u.id) openDetail(u.id);
    } catch (err) {
      toast.error(err.response?.data?.message || "İşlem başarısız.");
    }
  };

  const openDetail = async (id) => {
    try {
      const res = await fetchUser(id);
      setDetail(res.data);
    } catch {
      toast.error("Kullanıcı detayı getirilemedi.");
    }
  };

  return (
    <section>
      <h1>Kullanıcı Yönetimi</h1>

      <form className="bbm-filters" onSubmit={handleSearch}>
        <input
          type="search"
          placeholder="Ad, soyad veya e-posta ara..."
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <button type="submit" className="bbm-btn">Ara</button>
      </form>

      {loading ? (
        <p className="bbm-status">Yükleniyor...</p>
      ) : (
        <table className="bbm-table">
          <thead>
            <tr>
              <th>Ad Soyad</th>
              <th>E-posta</th>
              <th>Rol</th>
              <th>Sipariş</th>
              <th>Durum</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.firstName} {u.lastName}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>{u.orderCount}</td>
                <td>{u.active ? "Aktif" : "Pasif"}</td>
                <td className="bbm-row-actions">
                  <button type="button" className="bbm-linkbtn" onClick={() => openDetail(u.id)}>
                    Detay
                  </button>
                  <button
                    type="button"
                    className={`bbm-linkbtn${u.active ? " bbm-danger" : ""}`}
                    onClick={() => toggleActive(u)}
                  >
                    {u.active ? "Pasife al" : "Aktifleştir"}
                  </button>
                </td>
              </tr>
            ))}
            {users.length === 0 && (
              <tr><td colSpan={6} className="bbm-muted">Kullanıcı bulunamadı.</td></tr>
            )}
          </tbody>
        </table>
      )}

      {detail && (
        <div className="bbm-admin-detail">
          <div className="bbm-tab-header">
            <h3>{detail.user.firstName} {detail.user.lastName} — Siparişleri</h3>
            <button type="button" className="bbm-linkbtn" onClick={() => setDetail(null)}>Kapat</button>
          </div>
          {detail.orders.length === 0 ? (
            <p className="bbm-muted">Bu kullanıcının siparişi yok.</p>
          ) : (
            <table className="bbm-table">
              <thead>
                <tr><th>Sipariş No</th><th>Tarih</th><th>Tutar</th><th>Durum</th></tr>
              </thead>
              <tbody>
                {detail.orders.map((o) => (
                  <tr key={o.id}>
                    <td>{o.orderNumber}</td>
                    <td>{formatDate(o.createdAt)}</td>
                    <td>{formatPrice(o.totalPrice)}</td>
                    <td>{statusLabel(o.status)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </section>
  );
}
