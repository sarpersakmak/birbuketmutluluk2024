/**
 * Arka plan — ekranı sık dolduran, renkli, zarif çiçek/gonca/yaprak/sarmaşık figürleri.
 * Dengeli yayılır, yavaş süzülüp hafifçe döner.
 *
 * KRİTİK: pointer-events yok, z-index:0 (içerik z-index:1) → her zaman EN ARKADA.
 */

function Flower({ className, style }) {
  return (
    <svg className={className} style={style} viewBox="0 0 100 100" aria-hidden="true">
      <g fill="currentColor">
        <ellipse cx="50" cy="24" rx="12" ry="20" />
        <ellipse cx="50" cy="76" rx="12" ry="20" />
        <ellipse cx="24" cy="50" rx="20" ry="12" />
        <ellipse cx="76" cy="50" rx="20" ry="12" />
        <ellipse cx="32" cy="32" rx="12" ry="18" transform="rotate(45 32 32)" />
        <ellipse cx="68" cy="68" rx="12" ry="18" transform="rotate(45 68 68)" />
        <ellipse cx="68" cy="32" rx="12" ry="18" transform="rotate(-45 68 32)" />
        <ellipse cx="32" cy="68" rx="12" ry="18" transform="rotate(-45 32 68)" />
      </g>
      <circle cx="50" cy="50" r="10" fill="#ffffff" opacity="0.6" />
    </svg>
  );
}

function Bud({ className, style }) {
  return (
    <svg className={className} style={style} viewBox="0 0 100 100" aria-hidden="true">
      <g fill="currentColor">
        <path d="M50 16 C36 26 36 52 50 60 C64 52 64 26 50 16Z" />
      </g>
      <path d="M50 30 C44 38 44 52 50 58 C56 52 56 38 50 30Z" fill="#ffffff" opacity="0.35" />
      <g fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round">
        <path d="M40 54 C42 66 50 64 50 58" />
        <path d="M60 54 C58 66 50 64 50 58" />
        <path d="M50 60 L50 84" />
      </g>
    </svg>
  );
}

function Leaf({ className, style }) {
  return (
    <svg className={className} style={style} viewBox="0 0 100 100" aria-hidden="true">
      <path fill="currentColor" d="M50 90 C26 72 26 34 50 12 C74 34 74 72 50 90Z" />
      <path d="M50 22 L50 82" stroke="#ffffff" strokeWidth="2" opacity="0.4" fill="none" />
    </svg>
  );
}

function Vine({ className, style }) {
  return (
    <svg className={className} style={style} viewBox="0 0 100 100" aria-hidden="true">
      <path
        d="M14 88 C36 74 24 52 42 42 C58 33 46 18 68 12"
        fill="none"
        stroke="currentColor"
        strokeWidth="3"
        strokeLinecap="round"
      />
      <g fill="currentColor">
        <ellipse cx="29" cy="60" rx="9" ry="5" transform="rotate(45 29 60)" />
        <ellipse cx="45" cy="38" rx="9" ry="5" transform="rotate(-18 45 38)" />
        <ellipse cx="60" cy="18" rx="8" ry="5" transform="rotate(35 60 18)" />
      </g>
    </svg>
  );
}

const TYPES = [Flower, Bud, Flower, Vine, Flower, Bud, Leaf];
const FLOWER_COLORS = ["rose", "powder", "lilac", "peach", "yellow"];

// Ekranı sık ve dengeli dolduran figür ızgarası (~200 adet, deterministik).
function buildMotifs() {
  const items = [];
  const cols = 30;
  const rows = 24; // 30 * 24 = 720 figür
  let i = 0;
  for (let r = 0; r < rows; r++) {
    for (let c = 0; c < cols; c++) {
      // Hücre merkezleri + küçük deterministik kaydırma (organik görünüm).
      const jitterX = ((i * 17) % 7) - 3;
      const jitterY = ((i * 29) % 7) - 3;
      const left = (c + 0.5) * (100 / cols) + jitterX * 0.4;
      const top = (r + 0.5) * (100 / rows) + jitterY * 0.4;
      const Type = TYPES[i % TYPES.length];
      const isGreen = Type === Leaf || Type === Vine;
      const color = isGreen ? "leaf" : FLOWER_COLORS[i % FLOWER_COLORS.length];
      const small = i % 3 === 0;
      const anim = i % 2 === 0 ? "bbm-anim-a" : "bbm-anim-b";
      items.push({
        key: i,
        Type,
        className: `bbm-motif ${small ? "bbm-motif-sm" : ""} bbm-motif-${color} ${anim}`.trim(),
        style: {
          top: `${top}%`,
          left: `${left}%`,
          animationDuration: `${5 + (i % 5)}s`, // daha hızlı: 5-9 sn
          animationDelay: `-${(i * 0.5) % 6}s`,
        },
      });
      i++;
    }
  }
  return items;
}

const MOTIFS = buildMotifs();

export default function BotanicalOverlay() {
  return (
    <div className="bbm-botanical" aria-hidden="true">
      {MOTIFS.map(({ key, Type, className, style }) => (
        <Type key={key} className={className} style={style} />
      ))}
    </div>
  );
}
