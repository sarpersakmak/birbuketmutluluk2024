import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { useAuth } from "../../context/AuthContext";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [form, setForm] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const next = {};
    if (!EMAIL_REGEX.test(form.email)) next.email = "Geçerli bir e-posta adresi giriniz.";
    if (!form.password) next.password = "Şifre zorunludur.";
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    try {
      await login(form);
      toast.success("Giriş başarılı.");
      const redirectTo = location.state?.from?.pathname || "/";
      navigate(redirectTo, { replace: true });
    } catch (err) {
      const msg = err.response?.data?.message || "Giriş yapılamadı. Lütfen tekrar deneyin.";
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="bbm-auth">
      <h1>Giriş Yap</h1>
      <form className="bbm-form" onSubmit={handleSubmit} noValidate>
        <label className="bbm-field">
          <span>E-posta</span>
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            autoComplete="email"
          />
          {errors.email && <small className="bbm-error">{errors.email}</small>}
        </label>

        <label className="bbm-field">
          <span>Şifre</span>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            autoComplete="current-password"
          />
          {errors.password && <small className="bbm-error">{errors.password}</small>}
        </label>

        <button type="submit" className="bbm-btn" disabled={submitting}>
          {submitting ? "Giriş yapılıyor..." : "Giriş Yap"}
        </button>
      </form>

      <p className="bbm-muted">
        Hesabın yok mu? <Link to="/register">Kayıt ol</Link>
      </p>
    </section>
  );
}
