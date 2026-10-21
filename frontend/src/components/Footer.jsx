import { Link } from "react-router-dom";

const INSTAGRAM_URL = "https://instagram.com/birbuketmutluluk2024";

export default function Footer() {
  return (
    <footer className="bbm-footer">
      <div className="bbm-footer-inner">
        <div className="bbm-footer-col">
          <div className="bbm-brand bbm-footer-brand">
            <span className="bbm-brand-mark">❀</span> Bir Buket Mutluluk
          </div>
          <p className="bbm-footer-text">
            El yapımı soğuk porselen çiçekler ve zarif aranjmanlar. Solmayan güzellik,
            kişiye özel dokunuşlar.
          </p>
        </div>

        <div className="bbm-footer-col">
          <h4>Keşfet</h4>
          <Link to="/">Ana Sayfa</Link>
          <Link to="/urunler">Ürünler</Link>
          <Link to="/cart">Sepet</Link>
        </div>

        <div className="bbm-footer-col">
          <h4>Bizi Takip Et</h4>
          <a href={INSTAGRAM_URL} target="_blank" rel="noopener noreferrer" className="bbm-insta">
            <span className="bbm-brand-mark">◎</span> @birbuketmutluluk2024
          </a>
          <p className="bbm-footer-text">Yeni ürünler ve ilhamlar için Instagram'da bize katıl.</p>
        </div>
      </div>

      <div className="bbm-footer-bottom">
        © {"2024"} Bir Buket Mutluluk · Tüm hakları saklıdır.
      </div>
    </footer>
  );
}
