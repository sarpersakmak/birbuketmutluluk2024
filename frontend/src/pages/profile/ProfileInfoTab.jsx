import { useState } from "react";
import { toast } from "react-toastify";

import { useAuth } from "../../context/AuthContext";
import { changePassword, updateProfile } from "../../services/userService";

export default function ProfileInfoTab() {
  const { user, refreshUser } = useAuth();

  const [profile, setProfile] = useState({
    firstName: user?.firstName || "",
    lastName: user?.lastName || "",
    phone: user?.phone || "",
  });
  const [savingProfile, setSavingProfile] = useState(false);

  const [pwd, setPwd] = useState({ currentPassword: "", newPassword: "", confirm: "" });
  const [savingPwd, setSavingPwd] = useState(false);

  const changeProfile = (e) => setProfile({ ...profile, [e.target.name]: e.target.value });
  const changePwd = (e) => setPwd({ ...pwd, [e.target.name]: e.target.value });

  const submitProfile = async (e) => {
    e.preventDefault();
    if (!profile.firstName.trim() || !profile.lastName.trim()) {
      toast.error("Ad ve soyad zorunludur.");
      return;
    }
    setSavingProfile(true);
    try {
      await updateProfile(profile);
      await refreshUser();
      toast.success("Profiliniz güncellendi.");
    } catch (err) {
      toast.error(err.response?.data?.message || "Profil güncellenemedi.");
    } finally {
      setSavingProfile(false);
    }
  };

  const submitPwd = async (e) => {
    e.preventDefault();
    if (pwd.newPassword.length < 8) {
      toast.error("Yeni şifre en az 8 karakter olmalıdır.");
      return;
    }
    if (pwd.newPassword !== pwd.confirm) {
      toast.error("Yeni şifreler eşleşmiyor.");
      return;
    }
    setSavingPwd(true);
    try {
      await changePassword({ currentPassword: pwd.currentPassword, newPassword: pwd.newPassword });
      toast.success("Şifreniz güncellendi.");
      setPwd({ currentPassword: "", newPassword: "", confirm: "" });
    } catch (err) {
      toast.error(err.response?.data?.message || "Şifre değiştirilemedi.");
    } finally {
      setSavingPwd(false);
    }
  };

  return (
    <div className="bbm-tab-panel">
      <form className="bbm-admin-form" onSubmit={submitProfile}>
        <h3>Profil Bilgilerim</h3>
        <label className="bbm-field">
          <span>E-posta (değiştirilemez)</span>
          <input type="email" value={user?.email || ""} disabled />
        </label>
        <div className="bbm-form-row">
          <label className="bbm-field">
            <span className="bbm-field-label">Ad</span>
            <input name="firstName" placeholder="Adınız" value={profile.firstName} onChange={changeProfile} />
          </label>
          <label className="bbm-field">
            <span className="bbm-field-label">Soyad</span>
            <input name="lastName" placeholder="Soyadınız" value={profile.lastName} onChange={changeProfile} />
          </label>
        </div>
        <label className="bbm-field">
          <span className="bbm-field-label">Telefon</span>
          <input name="phone" placeholder="05xx xxx xx xx" value={profile.phone} onChange={changeProfile} />
        </label>
        <div className="bbm-form-actions">
          <button type="submit" className="bbm-btn" disabled={savingProfile}>
            {savingProfile ? "Kaydediliyor..." : "Bilgileri Güncelle"}
          </button>
        </div>
      </form>

      <form className="bbm-admin-form" onSubmit={submitPwd}>
        <h3>Şifre Değiştir</h3>
        <label className="bbm-field">
          <span className="bbm-field-label">Mevcut Şifre</span>
          <input
            type="password"
            name="currentPassword"
            placeholder="Mevcut şifreniz"
            value={pwd.currentPassword}
            onChange={changePwd}
            autoComplete="current-password"
          />
        </label>
        <div className="bbm-form-row">
          <label className="bbm-field">
            <span className="bbm-field-label">Yeni Şifre</span>
            <input
              type="password"
              name="newPassword"
              placeholder="En az 8 karakter"
              value={pwd.newPassword}
              onChange={changePwd}
              autoComplete="new-password"
            />
          </label>
          <label className="bbm-field">
            <span className="bbm-field-label">Yeni Şifre (Tekrar)</span>
            <input
              type="password"
              name="confirm"
              placeholder="Yeni şifre tekrar"
              value={pwd.confirm}
              onChange={changePwd}
              autoComplete="new-password"
            />
          </label>
        </div>
        <div className="bbm-form-actions">
          <button type="submit" className="bbm-btn" disabled={savingPwd}>
            {savingPwd ? "Kaydediliyor..." : "Şifreyi Değiştir"}
          </button>
        </div>
      </form>
    </div>
  );
}
