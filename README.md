<div align="center">

# 🌸 Bir Buket Mutluluk 2024 — E-Ticaret

El yapımı soğuk porselen çiçek ve aranjmanları satan **[@birbuketmutluluk2024](https://instagram.com/birbuketmutluluk2024)** markası için geliştirilen tam yığın (full-stack) e-ticaret web sitesi.

A full-stack e-commerce website built for **[@birbuketmutluluk2024](https://instagram.com/birbuketmutluluk2024)**, a brand selling handmade cold-porcelain flowers and arrangements.

**[🇹🇷 Türkçe](#-türkçe)  ·  [🇬🇧 English](#-english)**

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![React](https://img.shields.io/badge/React-18-blue)
![Vite](https://img.shields.io/badge/Vite-8-purple)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)

</div>

---

## 🇹🇷 Türkçe

### Hakkında
El yapımı soğuk porselen çiçek ve aranjmanları için tasarlanmış, Instagram odaklı bir butik işletmenin tam kapsamlı e-ticaret sitesi. Müşteriler ürünleri inceleyip sepete ekleyebilir ve sipariş verebilir; yönetici (admin) ürünleri, siparişleri ve kullanıcıları panelden yönetebilir.

### Teknoloji Yığını
- **Backend:** Java 21 (LTS) · Spring Boot 3.4.1 (Web, Data JPA, Security, Validation) · MySQL 8 · JWT (jjwt) · Lombok · Maven · springdoc/OpenAPI (Swagger)
- **Frontend:** React 18 · Vite · React Router · Axios · react-toastify
- **Veritabanı:** MySQL (geliştirme) · H2 (testler)

### Özellikler
- 🔐 Kayıt / giriş (JWT, BCrypt), rol bazlı erişim (USER / ADMIN)
- 🛍️ Ürün vitrini: arama, fiyat filtresi, sıralama, sayfalama, çoklu görselli ürün detayı (ok + lightbox)
- 🧺 Sepet (sunucu taraflı — fiyat ve toplamlar backend'de hesaplanır)
- 📦 Checkout + sipariş oluşturma (adres snapshot, stok düşümü, tek transaction)
- 👤 Kullanıcı paneli: profil, şifre değiştirme, adres defteri, sipariş geçmişi
- 🏙️ **İl / İlçe** veritabanından cascading (birbirine bağlı) açılır menüler (81 il)
- 🖼️ **Admin görsel yükleme** (bilgisayardan dosya ile; `uploads/` klasörüne kaydedilir)
- 🛠️ Admin paneli: dashboard, kullanıcı/ürün/sipariş yönetimi, sipariş durumu + iptalde stok iadesi
- 📱 **Hakkımızda** & **İletişim** sayfaları, **WhatsApp** sipariş butonu, Instagram bağlantıları
- 🌿 Marka odaklı responsive tasarım: hareketli çiçek arka planı, okunabilir içerik kartları, gül kurusu palet, özel favicon
- ⚙️ Merkezi hata yönetimi, Swagger/OpenAPI dokümantasyonu

### Proje Yapısı
```text
birbuketmutluluk2024/
├── backend/     # Spring Boot REST API (com.birbuketmutluluk)
├── frontend/    # React + Vite
├── database/    # 01_create_database.sql
├── .env.example # backend ortam değişkenleri örneği
└── README.md
```

### Önkoşullar
- Java 21 (LTS) · MySQL 8 · Node.js 18+ / npm · (Maven gerekmez — `mvnw` ile gelir)

### Kurulum & Çalıştırma (Geliştirme)

**1) Veritabanı** — MySQL Workbench'te [`database/01_create_database.sql`](database/01_create_database.sql) içindeki komutu çalıştırarak `bir_buket_mutluluk` veritabanını oluştur.

**2) Ortam değişkenleri (backend)** — Gizli bilgiler ortam değişkeninden okunur. Windows'ta kalıcı tanımlama:
```powershell
setx DB_PASSWORD "mysql_root_sifren"
# (yeni terminal aç — setx mevcut terminalleri etkilemez)
```
> Env verilmezse dev profili varsayılan `123456` dener. Tüm değişkenler için bkz. [`.env.example`](.env.example).

**3) Backend**
```bash
cd backend
./mvnw clean spring-boot:run        # Windows: .\mvnw.cmd clean spring-boot:run
```
- API: http://localhost:8080 · Swagger: http://localhost:8080/swagger-ui.html · Sağlık: `/api/health`
- İlk açılışta (dev) bir **ADMIN** oluşturulur: `admin@birbuketmutluluk.local` / `admin1234`

> 💡 Backend'i **IDE Run düğmesiyle değil terminalden** ve **`clean`** ile başlat (IDE'nin Lombok'suz derlediği sınıflar `ClassFormatError`'a yol açabilir).

**4) Frontend**
```bash
cd frontend
npm install
npm run dev
```
Uygulama: http://localhost:5173 · API adresi için `frontend/.env` (`VITE_API_BASE_URL`).

### Test
```bash
cd backend
./mvnw clean test      # birim + H2 entegrasyon testleri
```

### API Özeti
| Alan | Uçlar |
|---|---|
| Auth | `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me` |
| Ürün | `GET /api/products`, `/api/products/{slug}`, `/api/products/featured` |
| Konum | `GET /api/provinces`, `GET /api/provinces/{id}/districts` |
| Sepet | `GET/DELETE /api/cart`, `POST/PUT/DELETE /api/cart/items[/{id}]` |
| Adres/Sipariş | `GET/POST/PUT/DELETE /api/addresses`, `POST /api/orders`, `GET /api/orders/my-orders` |
| Profil | `PUT /api/users/me`, `PUT /api/users/me/password` |
| Admin | `/api/admin/dashboard`, `/api/admin/users`, `/api/admin/orders`, `/api/admin/products`, `POST /api/admin/uploads` |

Tüm uçların ayrıntısı Swagger UI'da.

### Üretim (Production)
Prod profili sırları **yalnızca ortam değişkeninden** okur, Swagger kapalı, `ddl-auto=validate`.
```bash
# Backend — gerekli env: DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, CORS_ORIGINS
cd backend && ./mvnw clean package
java -jar target/bir-buket-mutluluk-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Frontend — .env içinde VITE_API_BASE_URL=https://<api-domaininiz>/api
cd frontend && npm run build          # çıktı: dist/
```

### Güvenlik Notları
- Şifreler BCrypt ile hash'lenir; response'larda şifre dönmez.
- JWT stateless; `/api/admin/**` yalnızca ADMIN; kullanıcı yalnızca kendi verisine erişir.
- Sipariş fiyat/stok/toplamı backend'de hesaplanır (frontend değerine güvenilmez).
- Sırlar Git'e konmaz (`.gitignore`); prod'da env zorunlu.

---

## 🇬🇧 English

### About
A complete e-commerce site for an Instagram-based boutique selling handmade cold-porcelain flowers and arrangements. Customers can browse products, add them to a cart and place orders; an administrator manages products, orders and users from an admin panel.

### Tech Stack
- **Backend:** Java 21 (LTS) · Spring Boot 3.4.1 (Web, Data JPA, Security, Validation) · MySQL 8 · JWT (jjwt) · Lombok · Maven · springdoc/OpenAPI (Swagger)
- **Frontend:** React 18 · Vite · React Router · Axios · react-toastify
- **Database:** MySQL (development) · H2 (tests)

### Features
- 🔐 Register / login (JWT, BCrypt), role-based access (USER / ADMIN)
- 🛍️ Product catalog: search, price filter, sorting, pagination, multi-image product detail (arrows + lightbox)
- 🧺 Cart (server-side — prices and totals computed on the backend)
- 📦 Checkout + order creation (address snapshot, stock decrement, single transaction)
- 👤 User area: profile, change password, address book, order history
- 🏙️ **Province / District** cascading dropdowns backed by the database (81 provinces)
- 🖼️ **Admin image upload** (from the computer; files stored under `uploads/`)
- 🛠️ Admin panel: dashboard, user/product/order management, order status + stock restock on cancellation
- 📱 **About** & **Contact** pages, **WhatsApp** order button, Instagram links
- 🌿 Brand-focused responsive design: animated floral background, readable content cards, dusty-rose palette, custom favicon
- ⚙️ Centralized error handling, Swagger/OpenAPI documentation

### Project Structure
```text
birbuketmutluluk2024/
├── backend/     # Spring Boot REST API (com.birbuketmutluluk)
├── frontend/    # React + Vite
├── database/    # 01_create_database.sql
├── .env.example # backend environment variables sample
└── README.md
```

### Prerequisites
- Java 21 (LTS) · MySQL 8 · Node.js 18+ / npm · (Maven not required — comes with `mvnw`)

### Setup & Run (Development)

**1) Database** — In MySQL Workbench, run the statement in [`database/01_create_database.sql`](database/01_create_database.sql) to create the `bir_buket_mutluluk` database.

**2) Environment variables (backend)** — Secrets are read from environment variables. On Windows, set one persistently:
```powershell
setx DB_PASSWORD "your_mysql_root_password"
# (open a new terminal — setx does not affect existing ones)
```
> If no env is set, the dev profile falls back to `123456`. See [`.env.example`](.env.example) for all variables.

**3) Backend**
```bash
cd backend
./mvnw clean spring-boot:run        # Windows: .\mvnw.cmd clean spring-boot:run
```
- API: http://localhost:8080 · Swagger: http://localhost:8080/swagger-ui.html · Health: `/api/health`
- On first run (dev) an **ADMIN** is seeded: `admin@birbuketmutluluk.local` / `admin1234`

> 💡 Start the backend **from the terminal (not the IDE Run button)** and with **`clean`** (classes the IDE compiles without Lombok can cause `ClassFormatError`).

**4) Frontend**
```bash
cd frontend
npm install
npm run dev
```
App: http://localhost:5173 · Set the API URL via `frontend/.env` (`VITE_API_BASE_URL`).

### Tests
```bash
cd backend
./mvnw clean test      # unit + H2 integration tests
```

### API Overview
| Area | Endpoints |
|---|---|
| Auth | `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me` |
| Products | `GET /api/products`, `/api/products/{slug}`, `/api/products/featured` |
| Location | `GET /api/provinces`, `GET /api/provinces/{id}/districts` |
| Cart | `GET/DELETE /api/cart`, `POST/PUT/DELETE /api/cart/items[/{id}]` |
| Address/Order | `GET/POST/PUT/DELETE /api/addresses`, `POST /api/orders`, `GET /api/orders/my-orders` |
| Profile | `PUT /api/users/me`, `PUT /api/users/me/password` |
| Admin | `/api/admin/dashboard`, `/api/admin/users`, `/api/admin/orders`, `/api/admin/products`, `POST /api/admin/uploads` |

Full details are available in Swagger UI.

### Production
The prod profile reads secrets **only from environment variables**, disables Swagger, and uses `ddl-auto=validate`.
```bash
# Backend — required env: DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, CORS_ORIGINS
cd backend && ./mvnw clean package
java -jar target/bir-buket-mutluluk-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Frontend — set VITE_API_BASE_URL=https://<your-api-domain>/api in .env
cd frontend && npm run build          # output: dist/
```

### Security Notes
- Passwords are hashed with BCrypt; passwords are never returned in responses.
- JWT is stateless; `/api/admin/**` is ADMIN-only; a user can access only their own data.
- Order prices/stock/totals are computed on the backend (frontend values are not trusted).
- Secrets are not committed to Git (`.gitignore`); env variables are mandatory in production.

---

<div align="center">

🌸 **Bir Buket Mutluluk 2024** · El yapımı soğuk porselen çiçekler / Handmade cold-porcelain flowers

</div>
