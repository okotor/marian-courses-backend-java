# Webová aplikace Kurzy Marie (v tomto repozitáři je pouze backend v Java Spring Boot)

**Moderní aplikace pro správu, prezentaci a správu online kurzů.**  
Tento projekt je fullstack aplikace: **frontend v Next.js, backend v Java Spring Boot**.

---

## 🚀 Hlavní funkce

- Moderní responzivní uživatelské rozhraní (React/Next.js)
- Přihlášení & registrace (vlastní, Google OAuth)
- JWT autentizace, refresh tokeny, multi-tab synchronizace
- Správa kurzů (vytváření, editace, mazání – pouze admin)
- Veřejný archiv kurzů, vyhledávání, řazení, ročníky
- Nahrávání obrázků na AWS S3
- Možnost filtrovat a zobrazit nejnovější kurzy
- Notifikace úspěchů/chyb (v češtině)
- Správa cookies & GDPR banner
- CI/CD: **Vercel (frontend)** & **Render (backend)**

---

## 📦 Repozitáře

- **Frontend (Next.js):**  
  [https://github.com/okotor/maria-courses-next-js-java-backend](https://github.com/okotor/maria-courses-next-js-java-backend)
- **Backend (Java Spring Boot):**  
  [https://github.com/okotor/marian-courses-backend-java](https://github.com/okotor/marian-courses-backend-java)

---

## 🛠️ Tech Stack

- **Frontend:** Next.js, React 19, Tailwind CSS, MUI, Emotion, Axios, NextAuth, Lucide, Headless UI
- **Backend:** Spring Boot, Spring Security, JWT, JPA, PostgreSQL, AWS S3, Google OAuth2
- **Databáze:** PostgreSQL
- **Deployment:** Vercel (frontend), Render (backend)
- **Styly:** CSS variables, Tailwind, moderní vzhled
- **CI/CD:** Automatické nasazení na každé změně

---

## 🏗️ Struktura projektu

/frontend (Next.js app)
/backend (Java Spring Boot)

- **frontend/**
  - `/app` – stránky, layouty, moduly Next.js (App Router)
  - `/components` – UI komponenty (hlavička, kurz, gridy, formuláře)
  - `/context` – Auth, loading, cookies, globální stav
  - `/utils` – api.js, constants.js, služby pro kurzy
  - `/styles` – Tailwind, globální CSS, proměnné
- **backend/**
  - `/src/main/java` – controllery, entity, služby, bezpečnost
  - `/src/main/resources` – `application.properties`
  - `/pom.xml` – Maven konfigurace

---

## 💻 Jak projekt spustit lokálně

### 1. Klonuj oba repozitáře:

```bash
git clone https://github.com/okotor/maria-courses-next-js-java-backend.git
git clone https://github.com/okotor/marian-courses-backend-java.git
2. Nastav si prostředí
Frontend (.env.local)
env
Copier
Modifier
NEXT_PUBLIC_BACKEND_URL=http://localhost:8080
NEXT_PUBLIC_GOOGLE_CLIENT_ID=...
NEXT_PUBLIC_GA_ID=...
Backend (.env nebo v Render nastavení)
env
Copier
Modifier
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/NAZEV_DB
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=...
FRONTEND_BASE_URL=http://localhost:3000
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=...
MAIL_PASSWORD=...
JWT_SECRET=nějaký-tajný-klíč
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
3. Backend – spuštění
bash
Copier
Modifier
cd marian-courses-backend-java
./mvnw spring-boot:run
4. Frontend – spuštění
bash
Copier
Modifier
cd maria-courses-next-js-java-backend
npm install
npm run dev
🌍 Produkce & CI/CD
Frontend je nasazován automaticky na Vercel

Backend běží na Render

🧑‍💻 Přispívání
Vytvoř branch: git checkout -b feature/něco

Commmitni změny a pushni na svůj fork

Vytvoř Pull Request do hlavního repozitáře

⚡ Rychlý přehled funkcí pro vývojáře
Autentizace: JWT tokeny v httpOnly cookies, refresh s každou expirací, synchronizace mezi taby přes BroadcastChannel.

API servis: /utils/api.js a /lib/courseService.js na frontend.

Chyby a úspěchy: Vše v češtině, notifikace i pro Google OAuth.

Správa uživatelů a kurzů: Ověření rolí (admin, běžný uživatel), restrikce přístupu.

Moderní styling: CSS proměnné, Google Fonts, wavy header.

📄 Licence
MIT
© 2025 tehacko





