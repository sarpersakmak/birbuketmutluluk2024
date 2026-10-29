import WhatsAppButton from "../components/WhatsAppButton";

const INSTAGRAM_URL = "https://instagram.com/birbuketmutluluk2024";

// Telefon numaraları. İkinci numara verildiğinde bu listeye eklenmesi yeterli:
//   { display: "05xx xxx xxxx", tel: "+905xxxxxxxxx" }
const PHONES = [
  { display: "0532 684 2003", tel: "+905326842003" },
  { display: "0538 794 2208", tel: "+905387942208" },
];

export default function ContactPage() {
  return (
    <section className="bbm-page bbm-contact">
      <div className="bbm-about-hero">
        <span className="bbm-hero-eyebrow">İletişim</span>
        <h1>Bize Ulaşın</h1>
        <p className="bbm-about-lead">
          Sipariş, özel tasarım ve sorularınız için bize Instagram veya telefon üzerinden
          ulaşabilirsiniz.
        </p>
      </div>

      <div className="bbm-contact-grid">
        <a href={INSTAGRAM_URL} target="_blank" rel="noopener noreferrer" className="bbm-contact-card">
          <span className="bbm-contact-icon">◎</span>
          <span className="bbm-contact-label">Instagram</span>
          <span className="bbm-contact-value">@birbuketmutluluk2024</span>
          <span className="bbm-contact-hint">Takip et & mesaj gönder</span>
        </a>

        {PHONES.map((p) => (
          <a key={p.tel} href={`tel:${p.tel}`} className="bbm-contact-card">
            <span className="bbm-contact-icon">✆</span>
            <span className="bbm-contact-label">Telefon</span>
            <span className="bbm-contact-value">{p.display}</span>
            <span className="bbm-contact-hint">Aramak için dokun</span>
          </a>
        ))}
      </div>

      <div className="bbm-wa-wrap">
        <WhatsAppButton
          text="WhatsApp ile Sipariş Ver"
          message="Merhaba, Bir Buket Mutluluk ürünleri hakkında bilgi almak istiyorum."
          className="bbm-wa-lg"
        />
      </div>

      <p className="bbm-muted bbm-contact-note">
        El yapımı ürünlerimiz siparişe özel hazırlandığı için üretim süresi ürüne göre değişebilir;
        detaylar için bizimle iletişime geçmekten çekinmeyin.
      </p>
    </section>
  );
}
