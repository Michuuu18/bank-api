# Bank API

Backend REST w architekturze wielowarstwowej (kontroler → serwis → repozytorium) dla uproszczonego systemu bankowego: klienci, rachunki, przelewy i historia transakcji. Projekt demonstracyjny przygotowany pod ekosystem **Java** i **Spring Boot**, z naciskiem na praktyki zbliżone do środowisk komercyjnych: wersjonowanie schematu bazy, walidację, podstawowe bezpieczeństwo haseł, dokumentację OpenAPI oraz testy automatyczne.

---

## Spis treści

1. [Stos technologiczny](#stos-technologiczny)
2. [Architektura i pakiety](#architektura-i-pakiety)
3. [Wymagania środowiskowe](#wymagania-środowiskowe)
4. [Profile Spring (`local`, `dev`, `prod`, testy)](#profile-spring-local-dev-prod-testy)
5. [Uruchomienie](#uruchomienie)
6. [Port serwera i konflikty](#port-serwera-i-konflikty)
7. [Baza danych i Flyway](#baza-danych-i-flyway)
8. [Bezpieczeństwo](#bezpieczeństwo)
9. [Dokumentacja API (OpenAPI 3 / Swagger UI)](#dokumentacja-api-openapi-3--swagger-ui)
10. [Endpointy REST](#endpointy-rest)
11. [Actuator](#actuator)
12. [Testy automatyczne](#testy-automatyczne)
13. [Struktura repozytorium](#struktura-repozytorium)

---

## Stos technologiczny

| Technologia | Rola |
|-------------|------|
| **Java 17** | Język i docelowa wersja JDK projektu (w `pom.xml`). |
| **Spring Boot 3.5.x** | Szkielet aplikacji (parent BOM). |
| **Spring Web** | REST API (Jackson, Tomcat). |
| **Spring Data JPA** | Persystencja, repozytoria. |
| **Bean Validation** | Walidacja wejścia (np. `@Valid`, constrainty na encjach). |
| **Spring Security** | Filtry HTTP, `PasswordEncoder` (BCrypt); obecnie endpointy są publiczne do kolejnych etapów autoryzacji. |
| **PostgreSQL** | Baza docelowa w profilu `dev` / `prod`. |
| **H2** | Baza w pamięci w profilu `local` i w testach. |
| **Flyway** | Wersjonowanie i migracje schematu (`src/main/resources/db/migration`). |
| **PostgreSQL support dla Flyway** | Moduł `flyway-database-postgresql` (kompatybilność z nowszymi wersjami PostgreSQL). |
| **springdoc-openapi** | Specyfikacja **OpenAPI 3** i **Swagger UI**. |
| **Lombok** | Redukcja boilerplate w wybranych klasach (np. `Account`). |
| **JUnit 5, AssertJ, Mockito** | Testy (moduł `spring-boot-starter-test`). |
| **Maven** | Budowanie i zależności (`mvnw` / `mvnw.cmd`). |

---

## Architektura i pakiety

- **`controller`** — warstwa HTTP: mapowanie ścieżek, delegacja do serwisów.
- **`service`** — logika biznesowa (m przelewy, rejestracja użytkownika z hashowaniem hasła).
- **`repository`** — Spring Data JPA (`JpaRepository`).
- **`model`** — encje JPA mapowane na tabele.
- **`dto`** — obiekty transferu (np. lista użytkowników bez hasła).
- **`config`** — konfiguracja Spring Security, OpenAPI.
- **`exception`** — globalna obsługa wyjątków (`GlobalExceptionHandler`).

Przepływ typowy: żądanie HTTP → kontroler → serwis → repozytorium → baza.

---

## Wymagania środowiskowe

- **JDK 17** (lub zgodny z projektem; uruchomienie na JDK nowszym jest możliwe, ale oficjalnie celowany jest Java 17).
- **Maven** — opcjonalnie globalnie; w repozytorium dostępny jest **Maven Wrapper** (`mvnw` / `mvnw.cmd`).
- **PostgreSQL** — tylko dla profili **`dev`** i **`prod`** (zainstalowany serwer i utworzona baza, np. `bank_db`).

---

## Profile Spring (`local`, `dev`, `prod`, testy)

Konfiguracja jest rozbita na pliki:

| Profil / kontekst | Plik / zachowanie | Baza | Flyway | JPA `ddl-auto` (profil) |
|-------------------|-------------------|------|--------|-------------------------|
| **`local`** (domyślny) | `application-local.properties` | H2 w pamięci (`bank_local`), tryb zbliżony do PostgreSQL | **Wyłączony** | `update` (schemat z encji) |
| **`dev`** | `application-dev.properties` | PostgreSQL (URL/użytkownik/hasło ze zmiennych lub domyślne w pliku) | **Włączony** + `baseline-on-migrate` | Dziedziczy z głównego `application.properties` (`none`) |
| **`prod`** | `application-prod.properties` | **Wymagane** `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Włączony, bez baseline | `none` |
| **Testy** | `src/test/resources/application.properties` | H2 `testdb` | Wyłączony | `create-drop` |

Wspólne ustawienia w **`application.properties`**: m.in. `spring.jpa.hibernate.ddl-auto=none` (gdy nie nadpisze profil), ścieżki Flyway, springdoc, domyślny profil `spring.profiles.default=local`.

**Aktywacja profilu:**

```text
# Windows (CMD)
set SPRING_PROFILES_ACTIVE=dev

# Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE="dev"

# Wszystkie systemy (JVM)
-Dspring.profiles.active=dev
```

Dla **`prod`** ustaw dodatkowo (przykład):

```text
DB_URL=jdbc:postgresql://localhost:5432/bank_db
DB_USERNAME=postgres
DB_PASSWORD=twoje_haslo
```

---

## Uruchomienie

**Kompilacja i testy:**

```bash
./mvnw.cmd clean test
```
(lub `mvnw clean test` w środowiskach Unix)

**Start aplikacji (profil domyślny `local` — bez Postgresa):**

```bash
./mvnw.cmd spring-boot:run
```

**Windows — zmniejszenie problemu „port zajęty”:** przed startem można zwolnić port 8081 i uruchomić Maven:

```bash
start-local.cmd
```
lub skrót:

```bash
run.cmd
```

**Z IDE:** uruchom klasę `pl.Pielichowski.bank_api.BankApiApplication`.

---

## Port serwera i konflikty

- Domyślny port: **`8081`** (zmienna `SERVER_PORT` lub `--server.port=...` go nadpisują).
- Jeśli **nie** podasz `--server.port=i` **oraz** nie uruchamiasz profilu **`prod`**, aplikacja przy starcie z `main` próbuje kolejno portów **8081 → 8082 → 8222 → 0** (ostatnia wartość oznacza port losowy przypisany przez system operacyjny), aby ograniczyć błąd „address already in use”.
- W profilu **`prod`** oraz przy jawnym **`--server.port=...`** mechanizm przełączania portów jest wyłączony — wtedy konflikt portu musisz rozwiązać środowiskowo (zatrzymanie drugiej instancji lub zmiana portu).

---

## Baza danych i Flyway

- Skrypty migracji: **`src/main/resources/db/migration`** (np. `V1__init_schema.sql`, `V2__harden_legacy_schema.sql`).
- Flyway jest **włączony** w konfiguracji bazowej; profil **`local`** go **wyłącza** i polega na H2 + `ddl-auto=update` dla szybkiego startu bez Postgresa.
- Profil **`dev`**: typowy zestaw PostgreSQL + Flyway; `spring.flyway.baseline-on-migrate=true` ułatwia istniejące bazy z już utworzonym schematem.
- Profil **`prod`**: `baseline-on-migrate=false` — migracje muszą być spójne z polityką wdrożeń (bez domyślnego baseline w kodzie).

Przed pierwszym uruchomieniem na PostgreSQL utwórz bazę (np. `bank_db`) i dopasuj URL oraz poświadczenia.

---

## Bezpieczeństwo

- **Spring Security** jest na ścieżce klas; konfiguracja w `SecurityConfig`: sesje stateless, CSRF wyłączony pod typowy styl REST (do rozbudowy o tokeny lub sesję według wymagań).
- **Hasła użytkowników:** przy zapisie (`UserService`) hasło jest kodowane **BCrypt**; w odpowiedziach JSON pole hasła nie jest serializowane (`User`).
- Wyłączono **`UserDetailsServiceAutoConfiguration`**, aby uniknąć generowania domyślnego użytkownika Spring z hasłem w logach.
- Ścieżki dokumentacji OpenAPI i Swagger UI są **jawnie** wpuszczone w konfiguracji Security (łatwe do utrzymania po włączeniu ochrony na `/api/**`).

---

## Dokumentacja API (OpenAPI 3 / Swagger UI)

Po uruchomieniu aplikacji (np. na `http://localhost:8081`):

| Zasób | Adres (przykład) |
|--------|------------------|
| **Swagger UI** | `http://localhost:8081/swagger-ui.html` (przekierowanie do `/swagger-ui/index.html`) |
| **Dokument JSON OpenAPI** | `http://localhost:8081/v3/api-docs` |

Metadane API (tytuł, wersja, serwer lokalny) są konfigurowane w `OpenApiConfig`.

---

## Endpointy REST

Poniżej zestawienie zachowania zgodnego z kodem kontrolerów. Bazowy host i port zależą od uruchomienia (np. `8081`).

### Użytkownicy — `/api/users`

| Metoda | Ścieżka | Opis |
|--------|---------|------|
| `GET` | `/api/users` | Lista użytkowników jako `UserDTO` (bez hasła). |
| `GET` | `/api/users/{id}` | Pojedynczy użytkownik encją `User` (hasło pomijane w JSON). |
| `POST` | `/api/users` | Rejestracja; ciało: `User` z polem `password` (plain); w bazie zapis BCrypt. |

### Rachunki — `/api/accounts`

| Metoda | Ścieżka | Opis |
|--------|---------|------|
| `GET` | `/api/accounts` | Lista rachunków. |
| `POST` | `/api/accounts` | Utworzenie rachunku (ciało: `Account` z powiązanym `user`). |
| `GET` | `/api/accounts/{id}` | Rachunek po identyfikatorze. |
| `POST` | `/api/accounts/transfer` | Przelew: parametry zapytania `fromId`, `toId`, `amount`, `title`. |

### Transakcje — `/api/transactions`

| Metoda | Ścieżka | Opis |
|--------|---------|------|
| `GET` | `/api/transactions` | Lista zapisanych transakcji. |

### Błędy

`GlobalExceptionHandler` mapuje `RuntimeException` na odpowiedź JSON ze statusem **404** i polami m.in. `message`, `timestamp`. W praktyce warto stopniowo węższe wyjątki domenowe i kody HTTP (np. 400, 409) — obecna forma jest uproszczona.

---

## Actuator

Domyślnie Spring Boot udostępnia m.in.:

```text
GET /actuator/health
```

Odpowiedź typu `{"status":"UP"}` potwierdza działanie kontekstu i zależności krytycznych (w tym połączenia z bazą w danym profilu).

---

## Testy automatyczne

Uruchomienie wszystkich testów:

```bash
./mvnw.cmd test
```

Wybrane zestawy:

| Klasa | Zakres |
|--------|--------|
| `BankApiApplicationTests` | Podniesienie kontekstu Spring. |
| `OpenApiDocumentationTest` | Dostępność `/v3/api-docs` i Swagger UI. |
| `UserServicePasswordTest` | Hash BCrypt przy tworzeniu użytkownika. |
| `AccountServiceTransferTest` | Logika przelewów z mockami repozytoriów (JUnit 5 + Mockito). |

---

## Struktura repozytorium

```text
bank-api/
├── mvnw, mvnw.cmd          # Maven Wrapper
├── pom.xml
├── start-local.cmd         # Opcjonalne zwolnienie portu 8081 i start (Windows)
├── run.cmd                 # Wywołanie start-local.cmd
├── README.md
├── src/main/java/pl/Pielichowski/bank_api/
│   ├── BankApiApplication.java
│   ├── config/             # Security, OpenAPI
│   ├── controller/
│   ├── dto/
│   ├── exception/
│   ├── model/
│   ├── repository/
│   └── service/
├── src/main/resources/
│   ├── application.properties
│   ├── application-local.properties
│   ├── application-dev.properties
│   ├── application-prod.properties
│   └── db/migration/       # Flyway
└── src/test/java/          # Testy jednostkowe i integracyjne kontekstu
```

---

## Podsumowanie

Bank API to spójny przykład usługi REST w Spring Boot z rozdzieleniem profili (**szybki start na H2** vs **PostgreSQL + Flyway**), podstawowym bezpieczeństwem haseł, udokumentowanym kontraktem (**Swagger**), migracjami bazy oraz testami krytycznych ścieżek (przelewy, OpenAPI, hasła). Dalszy rozwój typowo obejmuje: autoryzację na endpointach, precyzyjne kody błędów, integrację z realnym procesem CI/CD oraz twardą politykę `prod` (sekrety wyłącznie ze zmiennych środowiskowych lub menedżera tajemnic).
