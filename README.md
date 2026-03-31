# Bank API 🏦

Projekt demonstracyjny aplikacji backendowej dla systemu bankowego, zbudowany w oparciu o architekturę wielowarstwową i REST API. Tworzony w ramach przygotowań do komercyjnych projektów w ekosystemie Java/Spring.

## 🚀 Technologie
* **Java 17**
* **Spring Boot 3** (Web, Data JPA, Validation)
* **PostgreSQL** (główna baza danych)
* **Flyway** (wersjonowanie schematu bazy)
* **H2 Database** (testy)
* **Lombok** (redukcja kodu boilerplate)
* **Maven** (zarządzanie zależnościami)

## ⚙️ Uruchomienie lokalne
Aplikacja jest gotowa do uruchomienia typu "plug & play".
1. Sklonuj repozytorium.
2. Uruchom klasę `BankApiApplication` w swoim IDE.
3. Serwer wystartuje na porcie `8081`.

Domyślny profil to `local` (H2 in-memory), więc aplikacja uruchamia się bez zewnętrznej bazy.

Aby uruchomić profil PostgreSQL + Flyway:
- PowerShell: `$env:SPRING_PROFILES_ACTIVE="dev"; ./mvnw.cmd spring-boot:run`
- opcjonalnie ustaw zmienne: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

Migracje Flyway są wykonywane automatycznie w profilu `dev` z katalogu `src/main/resources/db/migration`.

## 📬 Dostępne Endpointy (Faza 1 - MVP)
**Użytkownicy (`/api/users`)**
* `GET /api/users` - pobieranie listy wszystkich klientów banku.
* `POST /api/users` - rejestracja nowego klienta.

**Konta bankowe (`/api/accounts`)**
* `GET /api/accounts` - pobieranie listy wszystkich rachunków.
* `POST /api/accounts` - otwieranie nowego rachunku dla istniejącego klienta.