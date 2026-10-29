import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import ProductCard from "../components/ProductCard";
import { fetchFeatured } from "../services/productService";

const TRUST_BADGES = [
  { icon: "✿", title: "El Yapımı Özen", text: "Her çiçek tek tek elde şekillendirilir." },
  { icon: "❀", title: "Solmayan Çiçekler", text: "Soğuk porselen; yıllarca ilk günkü gibi." },
  { icon: "🎁", title: "Özenli Paketleme", text: "Hediyeye hazır, zarif ambalaj." },
  { icon: "✎", title: "Kişiye Özel Tasarım", text: "İsteğine göre renk ve düzenleme." },
];

export default function HomePage() {
  const [featured, setFeatured] = useState([]);

  useEffect(() => {
    fetchFeatured().then((res) => setFeatured(res.data)).catch(() => setFeatured([]));
  }, []);

  return (
    <div className="bbm-home">
      {/* Hero */}
      <section className="bbm-hero">
        <div className="bbm-hero-content">
          <span className="bbm-hero-eyebrow">El Yapımı Soğuk Porselen</span>
          <h1 className="bbm-hero-title">Solmayan çiçeklerle<br />bir buket mutluluk</h1>
          <p className="bbm-hero-sub">
            Sevdiklerinize yıllarca solmayacak, el yapımı zarif çiçek aranjmanları.
            Kişiye özel tasarımlarla hazırlanır.
          </p>
          <div className="bbm-hero-actions">
            <Link to="/urunler" className="bbm-btn bbm-btn-lg">Ürünleri Keşfet</Link>
            <a
              href="https://instagram.com/birbuketmutluluk2024"
              target="_blank"
              rel="noopener noreferrer"
              className="bbm-btn bbm-btn-ghost bbm-btn-lg"
            >
              Instagram'da Gör
            </a>
          </div>
        </div>
        {/* Görsel alanı: ileride marka fotoğrafı buraya gelecek */}
        <div className="bbm-hero-media" aria-hidden="true">
          <div className="bbm-hero-orb" />
          <span className="bbm-hero-flower">❀</span>
        </div>
      </section>

      {/* Öne çıkan ürünler */}
      {featured.length > 0 && (
        <section className="bbm-section">
          <div className="bbm-section-head">
            <h2>Öne Çıkan Ürünler</h2>
            <Link to="/urunler" className="bbm-section-link">Tümünü gör →</Link>
          </div>
          <div className="bbm-grid">
            {featured.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </section>
      )}

      {/* Neden Biz? */}
      <section className="bbm-section bbm-trust">
        <div className="bbm-section-head bbm-center">
          <h2>Neden Bir Buket Mutluluk?</h2>
        </div>
        <div className="bbm-trust-grid">
          {TRUST_BADGES.map((b) => (
            <div className="bbm-trust-card" key={b.title}>
              <span className="bbm-trust-icon">{b.icon}</span>
              <h3>{b.title}</h3>
              <p>{b.text}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Instagram CTA */}
      <section className="bbm-cta-band">
        <h2>Instagram'da bizi takip et</h2>
        <p>Yeni ürünler, ilhamlar ve kampanyalar için.</p>
        <a
          href="https://instagram.com/birbuketmutluluk2024"
          target="_blank"
          rel="noopener noreferrer"
          className="bbm-btn bbm-btn-lg"
        >
          @birbuketmutluluk2024
        </a>
      </section>
    </div>
  );
}
