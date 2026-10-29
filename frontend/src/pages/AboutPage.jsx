import { Link } from "react-router-dom";

const INSTAGRAM_URL = "https://instagram.com/birbuketmutluluk2024";

const SKILLS = [
  "Soğuk Porselen Çiçek Sanatçısı",
  "Tasarımcı",
  "Eğitimci",
  "Online Ders",
  "Workshop",
];

export default function AboutPage() {
  return (
    <section className="bbm-page bbm-about">
      <div className="bbm-about-hero">
        <span className="bbm-hero-eyebrow">Hakkımızda</span>
        <h1>Bir Buket Mutluluk</h1>
        <p className="bbm-about-lead">
          Her bir çiçeğin tek tek elde şekillendirildiği, solmayan bir güzellik atölyesi.
        </p>
      </div>

      <div className="bbm-about-body">
        <p>
          <strong>Bir Buket Mutluluk</strong>, soğuk porselenden el yapımı çiçekler ve
          aranjmanlar hazırlayan butik bir atölyedir. Tüm tasarımlar
          <strong> Hatice Atar</strong> tarafından, sabırla ve özenle elde şekillendirilir.
        </p>
        <p>
          Soğuk porselen çiçekler solmaz; yıllar geçse de ilk günkü zarafetini korur. Bu yüzden
          her buket, sevdiklerinize kalıcı bir mutluluk bırakmanın en zarif yoludur. Doğum günü,
          yıldönümü, ev dekoru ya da özel bir sürpriz için kişiye özel tasarımlar hazırlanır.
        </p>
        <p>
          Hatice Atar aynı zamanda bir eğitimcidir; <strong>online ders</strong> ve
          <strong> workshop</strong>'larla soğuk porselen çiçek sanatını paylaşır, bu zanaatı
          daha çok kişiye ulaştırmayı hedefler.
        </p>

        <div className="bbm-about-skills">
          {SKILLS.map((s) => (
            <span key={s} className="bbm-chip">{s}</span>
          ))}
        </div>

        <div className="bbm-about-cta">
          <Link to="/urunler" className="bbm-btn bbm-btn-lg">Ürünleri Keşfet</Link>
          <a
            href={INSTAGRAM_URL}
            target="_blank"
            rel="noopener noreferrer"
            className="bbm-btn bbm-btn-ghost bbm-btn-lg"
          >
            Instagram'da Gör
          </a>
          <Link to="/iletisim" className="bbm-btn bbm-btn-ghost bbm-btn-lg">İletişime Geç</Link>
        </div>
      </div>
    </section>
  );
}
