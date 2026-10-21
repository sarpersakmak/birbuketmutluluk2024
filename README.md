# Bir Buket Mutluluk 2024 — E-Ticaret

El yapımı soğuk porselen çiçek ve aranjmanları satan **@birbuketmutluluk2024** markası için
geliştirilen tam yığın (full-stack) e-ticaret web sitesi.

- **Backend:** Java 21 (LTS) · Spring Boot 3.4 (Web, Data JPA, Security, Validation) · MySQL · JWT · Maven
- **Frontend:** React · Vite · React Router · Axios · react-toastify

Durum: **Aşama 1–10 tamamlandı** (mimari, auth, ürün/kategori, sepet, sipariş, kullanıcı paneli,
admin paneli, tasarım, güvenlik, test/temizlik/deploy hazırlığı).

---

## Özellikler

- Kayıt / giriş (JWT, BCrypt), rol bazlı erişim (USER / ADMIN)
- Ürün vitrini: arama, kategori/fiyat filtresi, sıralama, sayfalama, ürün detayı
- Sepet (sunucu taraflı, güvenli fiyat/toplam)
- Checkout + sipariş oluşturma (adres snapshot, stok düşümü, tek transaction)
- Kullanıcı paneli: profil, şifre değiştirme, adres defteri, sipariş geçmişi
- Admin paneli: dashboard, kullanıcı/ürün/kategori/sipariş yönetimi, sipariş durumu + iptalde stok iadesi
- Marka odaklı responsive tasarım, merkezi hata yönetimi, Swagger/OpenAPI

## Proje Yapısı

```text
birbuketmutluluk2024/
├── backend/     # Spring Boot REST API (com.birbuketmutluluk)
├── frontend/    # React + Vite
├── database/    # 01_create_database.sql
├── .env.example # backend ortam değişkenleri örneği
└── README.md
```

## Önkoşullar
- Java 21 (LTS) · MySQL 8 · Node.js 18+ / npm · (Maven gerekmez — `mvnw` ile gelir)

## Kurulum & Çalıştırma (Geliştirme)

### 1) Veritabanı
MySQL Workbench'te [`database/01_create_database.sql`](database/01_create_database.sql) içindeki
komutu çalıştırarak `bir_buket_mutluluk` veritabanını oluştur.

### 2) Ortam değişkenleri (backend)
Gizli bilgiler ortam değişkeninden okunur. En basiti kalıcı tanımlamak (Windows):
```powershell
setx DB_PASSWORD "mysql_root_sifren"
# (yeni terminal aç — setx mevcut terminalleri etkilemez)
```
> Env verilmezse dev profili varsayılan `123456` dener. Gerçek şifren farklıysa yukarıdaki gibi ver.
> Tüm değişkenler için bkz. [`.env.example`](.env.example).

### 3) Backend
```bash
cd backend
./mvnw clean spring-boot:run        # Windows: .\mvnw.cmd clean spring-boot:run
```
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Sağlık: http://localhost:8080/api/health
- İlk açılışta (dev) örnek kategori/ürün ve bir **ADMIN** oluşturulur:
  `admin@birbuketmutluluk.local` / `admin1234`

> İpucu: Backend'i **IDE Run düğmesiyle değil terminalden** ve **`clean`** ile başlat
> (IDE'nin Lombok'suz derlediği sınıflar `ClassFormatError`'a yol açabilir).

### 4) Frontend
```bash
cd frontend
npm install
npm run dev
```
Uygulama: http://localhost:5173 · API adresini değiştirmek için `frontend/.env` (`VITE_API_BASE_URL`).

## Test
```bash
cd backend
./mvnw clean test      # 35 test (birim + H2 entegrasyon)
```

## API Özeti
| Alan | Uçlar |
|---|---|
| Auth | `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me` |
| Ürün/Kategori | `GET /api/products`, `/api/products/{slug}`, `/api/products/featured`, `GET /api/categories` |
| Sepet | `GET/DELETE /api/cart`, `POST/PUT/DELETE /api/cart/items[/{id}]` |
| Adres/Sipariş | `GET/POST/PUT/DELETE /api/addresses`, `POST /api/orders`, `GET /api/orders/my-orders`, `/api/orders/{id}` |
| Profil | `PUT /api/users/me`, `PUT /api/users/me/password` |
| Admin | `/api/admin/dashboard`, `/api/admin/users`, `/api/admin/orders`, `/api/admin/products`, `/api/admin/categories` |

Tüm uçların ayrıntısı Swagger UI'da.

## Üretim (Production)
Prod profili sırları **yalnızca ortam değişkeninden** okur, Swagger kapalı, `ddl-auto=validate`.

**Backend**
```bash
cd backend
./mvnw clean package
# Gerekli env: DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, CORS_ORIGINS
java -jar target/bir-buket-mutluluk-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
**Frontend**
```bash
cd frontend
# .env içinde VITE_API_BASE_URL=https://<api-domaininiz>/api
npm run build          # çıktı: dist/  (statik olarak sunulur)
```

## Güvenlik Notları
- Şifreler BCrypt ile hash'lenir; response'larda password dönmez.
- JWT stateless; `/api/admin/**` yalnızca ADMIN; kullanıcı yalnızca kendi verisine erişir.
- Sipariş fiyat/stok/toplamı backend'de hesaplanır (frontend değerine güvenilmez).
- Sırlar Git'e konmaz (`.gitignore`); prod'da env zorunlu.

## Geliştirme Aşamaları
1. Mimari ✅ 2. Auth ✅ 3. Ürün/Kategori ✅ 4. Sepet ✅ 5. Adres/Sipariş ✅
6. Kullanıcı Paneli ✅ 7. Admin ✅ 8. Tasarım ✅ 9. Güvenlik ✅ 10. Test/Temizlik/Deploy ✅
