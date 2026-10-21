import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { useAuth } from "../../context/AuthContext";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phone: "",
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const next = {};
    if (!form.firstName.trim()) next.firstName = "Ad zorunludur.";
    if (!form.lastName.trim()) next.lastName = "Soyad zorunludur.";
    if (!EMAIL_REGEX.test(form.email)) next.email = "Geçerli bir e-posta adresi giriniz.";
    if (form.password.length < 8) next.password = "Şifre en az 8 karakter olmalıdır.";
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    try {
      await register(form);
      toast.success("Kayıt başarılı. Hoş geldiniz!");
      navigate("/", { replace: true });
    } catch (err) {
      const msg = err.response?.data?.message || "Kayıt yapılamadı. Lütfen tekrar deneyin.";
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="bbm-auth">
      <h1>Kayıt Ol</h1>
      <form className="bbm-form" onSubmit={handleSubmit} noValidate>
        <label className="bbm-field">
          <span>Ad</span>
          <input type="text" name="firstName" value={form.firstName} onChange={handleChange} />
          {errors.firstName && <small className="bbm-error">{errors.firstName}</small>}
        </label>

        <label className="bbm-field">
          <span>Soyad</span>
          <input type="text" name="lastName" value={form.lastName} onChange={handleChange} />
          {errors.lastName && <small className="bbm-error">{errors.lastName}</small>}
        </label>

        <label className="bbm-field">
          <span>E-posta</span>
          <input type="email" name="email" value={form.email} onChange={handleChange} autoComplete="email" />
          {errors.email && <small className="bbm-error">{errors.email}</small>}
        </label>

        <label className="bbm-field">
          <span>Şifre</span>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            autoComplete="new-password"
          />
          {errors.password && <small className="bbm-error">{errors.password}</small>}
        </label>

        <label className="bbm-field">
          <span>Telefon (opsiyonel)</span>
          <input type="tel" name="phone" value={form.phone} onChange={handleChange} autoComplete="tel" />
        </label>

        <button type="submit" className="bbm-btn" disabled={submitting}>
          {submitting ? "Kaydediliyor..." : "Kayıt Ol"}
        </button>
      </form>

      <p className="bbm-muted">
        Zaten hesabın var mı? <Link to="/login">Giriş yap</Link>
      </p>
    </section>
  );
}
