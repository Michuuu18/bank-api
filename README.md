# Bank API 🏦

Projekt demonstracyjny aplikacji backendowej dla systemu bankowego, zbudowany w oparciu o architekturę wielowarstwową i REST API. Tworzony w ramach przygotowań do komercyjnych projektów w ekosystemie Java/Spring.

## 🚀 Technologie
* **Java 17**
* **Spring Boot 3** (Web, Data JPA)
* **H2 Database** (in-memory, do celów deweloperskich)
* **Lombok** (redukcja kodu boilerplate)
* **Maven** (zarządzanie zależnościami)

## ⚙️ Uruchomienie lokalne
Aplikacja jest gotowa do uruchomienia typu "plug & play".
1. Sklonuj repozytorium.
2. Uruchom klasę `BankApiApplication` w swoim IDE.
3. Serwer wystartuje na porcie `8081`.

Konsola bazy danych H2 znajduje się pod adresem: `http://localhost:8081/h2-console`
* **JDBC URL:** `jdbc:h2:mem:bank_db`
* **User:** `sa`
* **Password:** *(puste)*

## 📬 Dostępne Endpointy (Faza 1 - MVP)
**Użytkownicy (`/api/users`)**
* `GET /api/users` - pobieranie listy wszystkich klientów banku.
* `POST /api/users` - rejestracja nowego klienta.

**Konta bankowe (`/api/accounts`)**
* `GET /api/accounts` - pobieranie listy wszystkich rachunków.
* `POST /api/accounts` - otwieranie nowego rachunku dla istniejącego klienta.